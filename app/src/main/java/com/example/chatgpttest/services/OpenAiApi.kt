package com.example.chatgpttest.services

import com.example.chatgpttest.models.networkModels.ResponseDto
import com.example.chatgpttest.models.networkModels.ResponseRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAiApi {
    @POST("responses")
    suspend fun generateResponse(@Body request: ResponseRequest): Response<ResponseDto?>

    @Streaming
    @POST("responses")
    suspend fun generateResponseStream(@Body request: ResponseRequest): ResponseBody
}