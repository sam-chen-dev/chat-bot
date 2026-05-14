package com.example.chatgpttest.networkModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRequest(
    val model: String,
    val input: String,
    val stream: Boolean,
    @SerialName("previous_response_id")
    val previousResponseId: String?
)