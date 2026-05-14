package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class CompletedResponse(
    val response: CompletedResponseData
)