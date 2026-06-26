package com.example.chatgpttest.features.streamChat

import com.example.chatgpttest.models.domainModels.ChatMessage

data class StreamChatUiState(
    val chatMessages: List<ChatMessage>,
    val onSendClick: () -> Unit,
    val onNewChatClick: () -> Unit
)