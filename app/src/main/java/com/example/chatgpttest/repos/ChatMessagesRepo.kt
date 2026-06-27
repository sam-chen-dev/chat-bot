package com.example.chatgpttest.repos

import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatMessagesRepo {
    // Messages
    suspend fun insert(chatMessage: ChatMessage): Long
    suspend fun update(chatMessage: ChatMessage): Int
    suspend fun deleteMessage(messageId: Long)
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>>
    suspend fun deleteAll()

    // Conversations
    suspend fun createConversation(title: String): Long
    fun getAllConversations(): Flow<List<Conversation>>
    fun searchConversations(query: String): Flow<List<Conversation>>
    suspend fun deleteConversation(conversationId: Long)
}
