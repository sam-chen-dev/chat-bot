package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class DeltaResponse(
    val delta: String
)