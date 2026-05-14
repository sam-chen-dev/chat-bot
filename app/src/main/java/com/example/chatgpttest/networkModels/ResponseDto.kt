package com.example.chatgpttest.networkModels

import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    val output: List<ResponseOutput>
)