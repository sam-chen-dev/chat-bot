package com.example.chatgpttest.di

import com.example.chatgpttest.db.ChatGptDatabase
import com.example.chatgpttest.features.regularChat.RegularChatViewModel
import com.example.chatgpttest.features.streamChat.StreamChatViewModel
import com.example.chatgpttest.repos.ChatMessagesRepoImpl
import com.example.chatgpttest.repos.OpenAiRepoImpl
import com.example.chatgpttest.services.OpenAiService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Services*/
    single { OpenAiService(androidContext()).openAiApi }

    /*Database*/
    single { ChatGptDatabase.getInstance(androidContext()).chatMessagesDao }

    /*Repos*/
    single { OpenAiRepoImpl(get()) }
    single { ChatMessagesRepoImpl(get()) }

    /*ViewModels*/
    viewModel { RegularChatViewModel(get()) }
    viewModel { StreamChatViewModel(get(), get()) }
}