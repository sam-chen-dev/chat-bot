package com.example.chatgpttest.networkModels

sealed interface ResponseEvent {
    data class Delta(val text: String) : ResponseEvent
    data class Completed(val responseId: String) : ResponseEvent
}