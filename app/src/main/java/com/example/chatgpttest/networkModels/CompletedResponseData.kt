package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class CompletedResponseData(
    val id: String
)