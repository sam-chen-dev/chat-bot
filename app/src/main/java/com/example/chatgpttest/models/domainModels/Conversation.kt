package com.example.chatgpttest.models.domainModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val lastUpdate: Long
)
