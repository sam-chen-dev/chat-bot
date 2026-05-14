package com.example.chatgpttest.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chatgpttest.models.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatMessage: ChatMessage): Long

    @Update
    suspend fun update(chatMessage: ChatMessage): Int

    @Query("SELECT * FROM chatMessages ORDER BY creationTimeInMillis DESC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMessage>>
}