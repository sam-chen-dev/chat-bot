package com.example.chatgpttest.features.streamChat

import android.app.Application
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgpttest.managers.ConversationManager
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import com.example.chatgpttest.repos.ChatMessagesRepo
import com.example.chatgpttest.repos.OpenAiRepoImpl
import com.example.chatgpttest.utils.SenderUuid
import com.example.utlikotlin.toStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StreamChatViewModel(
    application: Application,
    private val chatMessagesRepo: ChatMessagesRepo,
    private val openAiRepo: OpenAiRepoImpl,
    private val conversationManager: ConversationManager
) : AndroidViewModel(application) {
    private val uiScope = viewModelScope
    private val _currentConversationId = MutableStateFlow<Long?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedVersions = MutableStateFlow<Map<Long?, Int>>(emptyMap())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _allMessages = _currentConversationId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else chatMessagesRepo.getMessagesForConversation(id)
    }.stateIn(uiScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState = combine(_allMessages, _selectedVersions, chatMessagesRepo.getAllConversations(), _currentConversationId, _searchQuery) { allMessages, versions, conversations, currentId, query ->
        val versionInfo = calculateVersionMap(allMessages)
        val displayMessages = filterMessagesByVersion(allMessages, versions)
        
        StreamChatUiState(
            chatMessages = displayMessages,
            conversations = conversations.filter { it.title.contains(query, ignoreCase = true) },
            currentConversationId = currentId,
            searchQuery = query,
            onSendClick = { imageUri -> sendChatMessage(imageUri) },
            onNewChatClick = ::startNewChat,
            onConversationClick = { 
                _currentConversationId.value = it 
                _selectedVersions.value = emptyMap()
            },
            onDeleteConversationClick = ::deleteConversation,
            onSearchQueryChange = { _searchQuery.value = it },
            onEditMessageClick = ::editMessage,
            onRegenerateResponseClick = ::regenerateResponse,
            onVersionChange = { parentId, newIndex -> 
                _selectedVersions.update { it + (parentId to newIndex) }
            },
            versionMap = versionInfo
        )
    }.toStateFlow(uiScope, createInitialUiState())

    val inputState = TextFieldState()

    private fun createInitialUiState() = StreamChatUiState(
        emptyList(), emptyList(), null, "", {}, {}, {}, {}, {}, {}, {}, { _, _ -> }, emptyMap()
    )

    private fun filterMessagesByVersion(allMessages: List<ChatMessage>, selectedVersions: Map<Long?, Int>): List<ChatMessage> {
        if (allMessages.isEmpty()) return emptyList()
        val grouped = allMessages.groupBy { it.parentId }
        val result = mutableListOf<ChatMessage>()
        
        var currentParentId: Long? = null
        val sortedRoots = grouped[null]?.sortedBy { it.creationTimeInMillis } ?: emptyList()
        
        if (sortedRoots.isNotEmpty()) {
            val rootIndex = selectedVersions[null] ?: (sortedRoots.size - 1)
            var activeMsg = sortedRoots.getOrNull(rootIndex) ?: sortedRoots.last()
            result.add(activeMsg)
            
            while (true) {
                val children = grouped[activeMsg.id]?.sortedBy { it.creationTimeInMillis } ?: emptyList()
                if (children.isEmpty()) break
                val childIndex = selectedVersions[activeMsg.id] ?: (children.size - 1)
                activeMsg = children.getOrNull(childIndex) ?: children.last()
                result.add(activeMsg)
            }
        }
        return result.reversed()
    }

    private fun calculateVersionMap(allMessages: List<ChatMessage>): Map<Long, Pair<Int, Int>> {
        val grouped = allMessages.groupBy { it.parentId }
        val versionMap = mutableMapOf<Long, Pair<Int, Int>>()
        grouped.forEach { (_, siblings) ->
            val sortedSiblings = siblings.sortedBy { it.creationTimeInMillis }
            sortedSiblings.forEachIndexed { index, message ->
                versionMap[message.id] = (index + 1) to siblings.size
            }
        }
        return versionMap
    }

    private fun startNewChat() = uiScope.launch {
        _currentConversationId.value = null
        _selectedVersions.value = emptyMap()
        inputState.clearText()
    }

    private fun deleteConversation(id: Long) = uiScope.launch {
        chatMessagesRepo.deleteConversation(id)
        if (_currentConversationId.value == id) {
            _currentConversationId.value = null
        }
    }

    private fun sendChatMessage(imageUri: String? = null) = uiScope.launch {
        try {
            val inputText = inputState.text.toString()
            if (inputText.isBlank() && imageUri == null) return@launch
            
            var convId = _currentConversationId.value
            if (convId == null) {
                convId = chatMessagesRepo.createConversation(if (inputText.isNotBlank()) inputText.take(25) else "New Chat")
                _currentConversationId.value = convId
            }

            inputState.clearText()
            val lastMsgId = _allMessages.value.sortedByDescending { it.creationTimeInMillis }.firstOrNull()?.id
            
            val chatMessage = ChatMessage(SenderUuid.ME, inputText, System.currentTimeMillis(), convId, imageUri, lastMsgId)
            val userMsgId = chatMessagesRepo.insert(chatMessage)
            generateAiResponse(convId, userMsgId)
        } catch (e: Exception) {
            Log.e("StreamChatVM", "Error: ${e.message}")
        }
    }

    private suspend fun generateAiResponse(convId: Long, parentId: Long) {
        var currentChatMessage = ChatMessage(SenderUuid.GPT, "", System.currentTimeMillis(), convId, null, parentId)
        val insertedId = chatMessagesRepo.insert(currentChatMessage)
        val mockResponse = "This is response version ${System.currentTimeMillis() % 100}. You can toggle between different answers using the arrows!"
        val words = mockResponse.split(" ")
        words.forEachIndexed { index, _ ->
            delay(30L)
            val currentText = words.take(index + 1).joinToString(" ")
            chatMessagesRepo.update(currentChatMessage.copy(text = currentText).apply { id = insertedId })
        }
    }

    private fun editMessage(message: ChatMessage) {
        inputState.setTextAndPlaceCursorAtEnd(message.text)
    }

    private fun regenerateResponse(aiMessage: ChatMessage) = uiScope.launch {
        val convId = _currentConversationId.value ?: return@launch
        generateAiResponse(convId, aiMessage.parentId ?: 0L)
    }
}
