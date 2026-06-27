package com.example.chatgpttest.repos

import com.example.chatgpttest.db.ChatMessagesDao
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import kotlinx.coroutines.flow.Flow

class ChatMessagesRepoImpl(private val chatMessagesDao: ChatMessagesDao) : ChatMessagesRepo {
    
    override suspend fun insert(chatMessage: ChatMessage): Long {
        return chatMessagesDao.insert(chatMessage)
    }

    override suspend fun update(chatMessage: ChatMessage): Int {
        return chatMessagesDao.update(chatMessage)
    }

    override suspend fun deleteMessage(messageId: Long) {
        chatMessagesDao.deleteMessageById(messageId)
    }

    override fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>> {
        return chatMessagesDao.getMessagesForConversation(conversationId)
    }

    override suspend fun deleteAll() {
        chatMessagesDao.deleteAllMessages()
    }

    override suspend fun createConversation(title: String): Long {
        return chatMessagesDao.insertConversation(
            Conversation(title = title, lastUpdate = System.currentTimeMillis())
        )
    }

    override fun getAllConversations(): Flow<List<Conversation>> {
        return chatMessagesDao.getAllConversations()
    }

    override fun searchConversations(query: String): Flow<List<Conversation>> {
        return chatMessagesDao.searchConversations(query)
    }

    override suspend fun deleteConversation(conversationId: Long) {
        chatMessagesDao.deleteConversation(conversationId)
        chatMessagesDao.deleteMessagesForConversation(conversationId)
    }
}
