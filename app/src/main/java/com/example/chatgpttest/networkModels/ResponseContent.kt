package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseContent(
    val text: String
)