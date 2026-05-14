package com.example.chatgpttest.repos

import com.example.chatgpttest.networkModels.ResponseDto
import com.example.chatgpttest.networkModels.ResponseEvent
import com.example.chatgpttest.networkModels.ResponseRequest
import kotlinx.coroutines.flow.Flow

interface OpenAiRepo {
    suspend fun generateResponse(request: ResponseRequest): ResponseDto?

    suspend fun generateResponseStream(request: ResponseRequest): Flow<ResponseEvent>
}