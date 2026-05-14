package com.example.chatgpttest.features.streamChat

import android.app.Application
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgpttest.managers.ConversationManager
import com.example.chatgpttest.models.ChatMessage
import com.example.chatgpttest.networkModels.ResponseEvent
import com.example.chatgpttest.networkModels.ResponseRequest
import com.example.chatgpttest.repos.ChatMessagesRepoImpl
import com.example.chatgpttest.repos.OpenAiRepoImpl
import com.example.chatgpttest.utils.SenderUuid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StreamChatViewModel(
    application: Application,
    private val chatMessagesRepo: ChatMessagesRepoImpl,
    private val openAiRepo: OpenAiRepoImpl,
    private val conversationManager: ConversationManager
) : AndroidViewModel(application) {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val chatMessages = chatMessagesRepo.chatMessages
    val inputState = TextFieldState()

    val uiState = _uiState.asStateFlow()

    init {

    }

    private fun createUiState(): StreamChatUiState = StreamChatUiState(
        ::sendChatMessage
    )

    private fun sendChatMessage() = uiScope.launch {
        try {
            val inputText = inputState.text.toString()

            if (inputText.isBlank()) return@launch
            inputState.clearText()

            val chatMessage = ChatMessage(SenderUuid.ME, inputText, System.currentTimeMillis())
            chatMessagesRepo.insert(chatMessage)

            val responseRequest = ResponseRequest(
                "gpt-5.4-nano-2026-03-17",
                inputText,
                true,
                conversationManager.getPreviousResponseId(getApplication())
            )

            var stringBuilder = StringBuilder()
            var insertedId = -1L

            openAiRepo.generateResponseStream(responseRequest).collect { responseEvent ->
                when (responseEvent) {
                    is ResponseEvent.Delta -> {
                        stringBuilder.append(responseEvent.text)

                        val text = stringBuilder.toString()

                        if (insertedId == -1L) {
                            val chatMessage = ChatMessage(SenderUuid.GPT, text, System.currentTimeMillis())
                            insertedId = chatMessagesRepo.insert(chatMessage)
                        } else {
                            val chatMessage = ChatMessage(SenderUuid.GPT, text, System.currentTimeMillis()).apply {
                                id = insertedId
                            }

                            chatMessagesRepo.update(chatMessage)
                        }

                        delay(100L)
                    }

                    is ResponseEvent.Completed -> {
                        conversationManager.savePreviousResponseId(getApplication(), responseEvent.responseId)

                        stringBuilder = StringBuilder()
                        insertedId = -1L
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("xxx", "Error: ${e.message.toString()}")
        }
    }
}