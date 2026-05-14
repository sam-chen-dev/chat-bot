package com.example.chatgpttest.features.regularChat

data class RegularChatUiState(
    val isShowProcessingDialog: Boolean,
    val onSubmitClick: () -> Unit
)