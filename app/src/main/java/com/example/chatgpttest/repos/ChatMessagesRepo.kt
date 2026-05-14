package com.example.chatgpttest.repos

import com.example.chatgpttest.models.ChatMessage

interface ChatMessagesRepo {
    suspend fun insert(chatMessage: ChatMessage): Long

    suspend fun update(chatMessage: ChatMessage): Int
}