package com.example.chatgpttest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class CompletedResponse(
    val response: CompletedResponseData
)