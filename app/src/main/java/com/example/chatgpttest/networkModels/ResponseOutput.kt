package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseOutput(
    val content: List<ResponseContent>
)