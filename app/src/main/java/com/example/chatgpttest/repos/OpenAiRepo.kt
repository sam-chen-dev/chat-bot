package com.example.chatgpttest.repos

import com.example.chatgpttest.models.networkModels.ResponseDto
import com.example.chatgpttest.models.networkModels.ResponseEvent
import com.example.chatgpttest.models.networkModels.ResponseRequest
import kotlinx.coroutines.flow.Flow

interface OpenAiRepo {
    suspend fun generateResponse(request: ResponseRequest): ResponseDto?

    suspend fun generateResponseStream(request: ResponseRequest): Flow<ResponseEvent>
}