package com.example.chatgpttest.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatgpttest.models.domainModels.ChatMessage

@Database(
    entities = [
        ChatMessage::class
    ],
    version = 1
)
abstract class ChatGptDatabase : RoomDatabase() {
    abstract val chatMessagesDao: ChatMessagesDao

    companion object {
        @Volatile
        private var INSTANCE: ChatGptDatabase? = null

        fun getInstance(context: Context): ChatGptDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        ChatGptDatabase::class.java,
                        "chat_gpt_database"
                    ).build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}