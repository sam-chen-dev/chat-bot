package com.example.chatgpttest.features.regularChat

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgpttest.networkModels.ResponseRequest
import com.example.chatgpttest.repos.OpenAiRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegularChatViewModel(private val openAiRepo: OpenAiRepoImpl) : ViewModel() {
    private val uiScope = viewModelScope
    private val _uiState = MutableStateFlow(createUiState())

    val inputState = TextFieldState()
    val outputState = TextFieldState()

    val uiState = _uiState.asStateFlow()

    init {

    }

    private fun createUiState(): RegularChatUiState = RegularChatUiState(
        false,
        ::submitText
    )

    private fun submitText() = uiScope.launch {
        try {
            updateIsShowProcessingDialog(true)

            val responseRequest = ResponseRequest("gpt-5.4-nano-2026-03-17", inputState.text.toString(), false)
            val responseDto = openAiRepo.generateResponse(responseRequest)

            responseDto?.output?.first()?.content?.first()?.text?.let { text ->
                outputState.setTextAndPlaceCursorAtEnd(text)
            }

            updateIsShowProcessingDialog(false)
        } catch (e: Exception) {
            Log.e("xxx", "Error: ${e.message.toString()}")
        }
    }

    private fun updateIsShowProcessingDialog(isShowProcessingDialog: Boolean) =
        _uiState.update { it.copy(isShowProcessingDialog = isShowProcessingDialog) }
}