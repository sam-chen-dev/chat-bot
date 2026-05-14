package com.example.chatgpttest.models.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    val output: List<ResponseOutput>
)