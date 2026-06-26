package com.example.chatgpttest.repos

import com.example.chatgpttest.db.ChatMessagesDao
import com.example.chatgpttest.models.domainModels.ChatMessage

class ChatMessagesRepoImpl(private val chatMessagesDao: ChatMessagesDao) : ChatMessagesRepo {
    val chatMessages = chatMessagesDao.getAllChatMessagesFlow()

    override suspend fun insert(chatMessage: ChatMessage): Long {
        return chatMessagesDao.insert(chatMessage)
    }

    override suspend fun update(chatMessage: ChatMessage): Int {
        return chatMessagesDao.update(chatMessage)
    }

    override suspend fun deleteAll() {
        chatMessagesDao.deleteAll()
    }
}