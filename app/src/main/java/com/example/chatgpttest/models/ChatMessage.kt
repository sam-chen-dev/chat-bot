package com.example.chatgpttest.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chatMessages")
data class ChatMessage(
    val senderUuid: String,
    val text: String,
    val creationTimeInMillis: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}