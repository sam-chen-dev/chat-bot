package com.example.chatgpttest.features.streamChat

import android.app.Application
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgpttest.enums.GptModel
import com.example.chatgpttest.managers.ConversationManager
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import com.example.chatgpttest.models.networkModels.ResponseRequest
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _chatMessages = _currentConversationId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else chatMessagesRepo.getMessagesForConversation(id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _conversations = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) chatMessagesRepo.getAllConversations()
        else chatMessagesRepo.searchConversations(query)
    }

    val uiState = combine(_chatMessages, _conversations, _currentConversationId, _searchQuery) { messages, conversations, currentId, query ->
        StreamChatUiState(
            chatMessages = messages,
            conversations = conversations,
            currentConversationId = currentId,
            searchQuery = query,
            onSendClick = { imageUri: String? -> sendChatMessage(imageUri) },
            onNewChatClick = ::startNewChat,
            onConversationClick = { _currentConversationId.value = it },
            onDeleteConversationClick = ::deleteConversation,
            onSearchQueryChange = { _searchQuery.value = it }
        )
    }.toStateFlow(uiScope, createInitialUiState())

    val inputState = TextFieldState()

    private fun createInitialUiState() = StreamChatUiState(
        emptyList(), emptyList(), null, "", {}, {}, {}, {}, {}
    )

    private fun startNewChat() = uiScope.launch {
        _currentConversationId.value = null
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
                // Create new conversation on first message
                val title = if (inputText.isNotBlank()) inputText.take(25) else "Image Message"
                convId = chatMessagesRepo.createConversation(title)
                _currentConversationId.value = convId
            }

            inputState.clearText()

            val chatMessage = ChatMessage(SenderUuid.ME, inputText, System.currentTimeMillis(), convId, imageUri)
            chatMessagesRepo.insert(chatMessage)

            val mockResponse = """
                Sure! Here's some code and math for you:
                
                ### Kotlin Code Example:
                ```kotlin
                fun main() {
                    println("Hello, Markdown!")
                }
                ```
                
                ### Mathematical Formulas:
                - **Quadratic Formula:** ${'$'}x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}${'$'}
                
                I hope this helps!
            """.trimIndent()

            var currentChatMessage = ChatMessage(SenderUuid.GPT, "", System.currentTimeMillis(), convId)
            val insertedId = chatMessagesRepo.insert(currentChatMessage)
            currentChatMessage = currentChatMessage.copy().apply { id = insertedId }
            
            val words = mockResponse.split(" ")
            words.forEachIndexed { index, _ ->
                delay(50L)
                val currentText = words.take(index + 1).joinToString(" ")
                currentChatMessage = currentChatMessage.copy(text = currentText).apply { id = insertedId }
                chatMessagesRepo.update(currentChatMessage)
            }

        } catch (e: Exception) {
            Log.e("StreamChatVM", "Error in sendChatMessage: ${e.message}", e)
        }
    }
}
