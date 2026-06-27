package com.example.chatgpttest.features.streamChat

import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation

data class StreamChatUiState(
    val chatMessages: List<ChatMessage>,
    val conversations: List<Conversation>,
    val currentConversationId: Long?,
    val searchQuery: String = "",
    val onSendClick: (String?) -> Unit,
    val onNewChatClick: () -> Unit,
    val onConversationClick: (Long) -> Unit,
    val onDeleteConversationClick: (Long) -> Unit,
    val onSearchQueryChange: (String) -> Unit
)
