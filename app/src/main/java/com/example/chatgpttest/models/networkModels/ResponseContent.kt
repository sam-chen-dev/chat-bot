package com.example.chatgpttest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseContent(
    val text: String
)