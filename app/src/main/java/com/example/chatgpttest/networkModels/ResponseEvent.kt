package com.example.chatgpttest.networkModels

sealed interface ResponseEvent {
    data class Delta(val text: String) : ResponseEvent
    data object Completed : ResponseEvent
}