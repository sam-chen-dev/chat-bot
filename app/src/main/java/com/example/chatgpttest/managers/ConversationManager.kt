package com.example.chatgpttest.managers

import android.content.Context
import com.example.chatgpttest.R
import com.example.utlikotlin.DataStore

object ConversationManager {
    suspend fun savePreviousResponseId(context: Context, id: String) {
        DataStore.saveString(context, R.string.previous_response_id_key, id)
    }

    fun getPreviousResponseId(context: Context): String? {
        return DataStore.getString(context, R.string.previous_response_id_key)
    }
}