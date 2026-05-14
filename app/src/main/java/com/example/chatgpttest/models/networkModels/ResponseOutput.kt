package com.example.chatgpttest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseOutput(
    val content: List<ResponseContent>
)