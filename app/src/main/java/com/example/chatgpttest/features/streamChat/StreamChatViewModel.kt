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
import com.example.chatgpttest.models.networkModels.ResponseRequest
import com.example.chatgpttest.repos.ChatMessagesRepoImpl
import com.example.chatgpttest.repos.OpenAiRepoImpl
import com.example.chatgpttest.utils.SenderUuid
import com.example.utlikotlin.toStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StreamChatViewModel(
    application: Application,
    private val chatMessagesRepo: ChatMessagesRepoImpl,
    private val openAiRepo: OpenAiRepoImpl,
    private val conversationManager: ConversationManager
) : AndroidViewModel(application) {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val uiState = combine(chatMessagesRepo.chatMessages, _uiState) { chatMessages, uiState ->
        uiState.copy(chatMessages = chatMessages)
    }.toStateFlow(uiScope, createUiState())

    val inputState = TextFieldState()

    init {

    }

    private fun createUiState(): StreamChatUiState = StreamChatUiState(
        emptyList(),
        ::sendChatMessage,
        ::startNewChat
    )

    private fun startNewChat() = uiScope.launch {
        chatMessagesRepo.deleteAll()
        conversationManager.clearConversation(getApplication())
    }

    private fun sendChatMessage() = uiScope.launch {
        try {
            val inputText = inputState.text.toString()

            if (inputText.isBlank()) return@launch
            inputState.clearText()

            Log.d("StreamChatVM", "Sending message: $inputText")

            val chatMessage = ChatMessage(SenderUuid.ME, inputText, System.currentTimeMillis())
            chatMessagesRepo.insert(chatMessage)

            val responseRequest = ResponseRequest(
                GptModel.NANO.id,
                inputText,
                true,
                conversationManager.getPreviousResponseId(getApplication())
            )

            Log.d("StreamChatVM", "Request: $responseRequest")

            val mockResponse = """
                Sure! Here's some code and math for you:
                
                ### Kotlin Code Example:
                ```kotlin
                fun main() {
                    println("Hello, Markdown!")
                    val sum = (1..10).sum()
                    println("Sum is: ${'$'}sum")
                }
                ```
                
                ### Mathematical Formulas:
                - **Quadratic Formula:** ${'$'}x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}${'$'}
                - **Euler's Identity:** ${'$'}e^{i\pi} + 1 = 0${'$'}
                - **Matrix Example:**
                  ${'$'}\begin{pmatrix} a & b \\ c & d \end{pmatrix}${'$'}
                
                I hope this helps with your markdown testing!
            """.trimIndent()

            var currentChatMessage = ChatMessage(SenderUuid.GPT, "", System.currentTimeMillis())
            val insertedId = chatMessagesRepo.insert(currentChatMessage)
            currentChatMessage = currentChatMessage.copy().apply { id = insertedId }
            Log.d("StreamChatVM", "Inserted GPT message placeholder with ID: $insertedId")
            
            val words = mockResponse.split(" ")
            words.forEachIndexed { index, _ ->
                delay(50L) // Simulate streaming
                val currentText = words.take(index + 1).joinToString(" ")
                currentChatMessage = currentChatMessage.copy(text = currentText).apply { id = insertedId }
                chatMessagesRepo.update(currentChatMessage)
                Log.d("StreamChatVM", "Updated GPT message ID $insertedId with text length: ${currentText.length}")
            }

            /*
            openAiRepo.generateResponseStream(responseRequest).collect { responseEvent ->
                Log.d("StreamChatVM", "Received event: ${'$'}responseEvent")
                when (responseEvent) {
                    is ResponseEvent.Delta -> {
                        stringBuilder.append(responseEvent.text)

                        val text = stringBuilder.toString()

                        if (insertedId == -1L) {
                            val chatMessage = ChatMessage(SenderUuid.GPT, text, System.currentTimeMillis())
                            insertedId = chatMessagesRepo.insert(chatMessage)
                            Log.d("StreamChatVM", "Inserted new GPT message with ID: ${'$'}insertedId")
                        } else {
                            val chatMessage = ChatMessage(SenderUuid.GPT, text, System.currentTimeMillis()).apply {
                                id = insertedId
                            }

                            chatMessagesRepo.update(chatMessage)
                        }

                        delay(100L)
                    }

                    is ResponseEvent.Completed -> {
                        Log.d("StreamChatVM", "Stream completed. ResponseID: ${'$'}{responseEvent.responseId}")
                        conversationManager.savePreviousResponseId(getApplication(), responseEvent.responseId)

                        stringBuilder = StringBuilder()
                        insertedId = -1L
                    }
                }
            }
            */
        } catch (e: Exception) {
            Log.e("StreamChatVM", "Error in sendChatMessage: ${e.message}", e)
        }
    }
}