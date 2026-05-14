package com.example.chatgpttest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class DeltaResponse(
    val delta: String
)