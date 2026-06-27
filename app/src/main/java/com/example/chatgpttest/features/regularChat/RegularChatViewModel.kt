package com.example.chatgpttest.features.regularChat

import android.app.Application
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgpttest.enums.GptModel
import com.example.chatgpttest.managers.ConversationManager
import com.example.chatgpttest.models.networkModels.ResponseRequest
import com.example.chatgpttest.repos.OpenAiRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegularChatViewModel(
    application: Application,
    private val openAiRepo: OpenAiRepoImpl,
    private val conversationManager: ConversationManager
) : AndroidViewModel(application) {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val inputState = TextFieldState()
    val outputState = TextFieldState()

    val uiState = _uiState.asStateFlow()

    init {

    }

    private fun createUiState(): RegularChatUiState = RegularChatUiState(
        false,
        { imageUri -> submitText(imageUri) }
    )

    private fun submitText(imageUri: String? = null) = uiScope.launch {
        try {
            updateIsShowProcessingDialog(true)
            
            // In a real app, we would handle the imageUri here (e.g., upload it or send it as base64)

            val mockResponse = """
                ### Regular Chat Mock Response
                
                This is a mock response with markdown and math for testing:
                ${if (imageUri != null) "\n*I see you attached an image! (URI: $imageUri)*\n" else ""}
                - **Bold Text**
                - *Italic Text*
                
                ```kotlin
                val message = "Hello from Regular Chat!"
                println(message)
                ```
                
                Mathematical formula:
                ${'$'}E = mc^2${'$'}
                
                And a block formula:
                ${'$'}${'$'} \int_{a}^{b} x^2 dx = \frac{b^3 - a^3}{3} ${'$'}${'$'}
            """.trimIndent()

            outputState.setTextAndPlaceCursorAtEnd(mockResponse)

            updateIsShowProcessingDialog(false)
        } catch (e: Exception) {
            updateIsShowProcessingDialog(false)
            Log.e("xxx", "Error: ${e.message.toString()}")
        }
    }

    private fun updateIsShowProcessingDialog(isShowProcessingDialog: Boolean) =
        _uiState.update { it.copy(isShowProcessingDialog = isShowProcessingDialog) }
}