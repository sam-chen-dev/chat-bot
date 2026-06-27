package com.example.chatgpttest.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessagesDao {
    // --- Messages ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatMessage: ChatMessage): Long

    @Update
    suspend fun update(chatMessage: ChatMessage): Int

    @Query("DELETE FROM chatMessages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    @Query("SELECT * FROM chatMessages WHERE conversationId = :conversationId ORDER BY creationTimeInMillis DESC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>>

    @Query("DELETE FROM chatMessages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)

    @Query("DELETE FROM chatMessages")
    suspend fun deleteAllMessages()

    // --- Conversations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation): Long

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("SELECT * FROM conversations ORDER BY lastUpdate DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE title LIKE '%' || :query || '%' ORDER BY lastUpdate DESC")
    fun searchConversations(query: String): Flow<List<Conversation>>

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: Long)
}
