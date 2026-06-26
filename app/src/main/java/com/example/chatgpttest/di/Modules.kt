package com.example.chatgpttest.di

import com.example.chatgpttest.db.ChatGptDatabase
import com.example.chatgpttest.features.regularChat.RegularChatViewModel
import com.example.chatgpttest.features.streamChat.StreamChatViewModel
import com.example.chatgpttest.managers.ConversationManager
import com.example.chatgpttest.repos.ChatMessagesRepo
import com.example.chatgpttest.repos.ChatMessagesRepoImpl
import com.example.chatgpttest.repos.OpenAiRepoImpl
import com.example.chatgpttest.services.OpenAiService
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    /*Managers*/
    single { ConversationManager }

    /*Services*/
    single { OpenAiService(androidContext()).openAiApi }

    /*Database*/
    single { ChatGptDatabase.getInstance(androidContext()).chatMessagesDao }

    /*Repos*/
    single { OpenAiRepoImpl(get()) }
    single<ChatMessagesRepo> { ChatMessagesRepoImpl(get()) }

    /*ViewModels*/
    viewModel { RegularChatViewModel(androidApplication(), get(), get()) }
    viewModel { StreamChatViewModel(androidApplication(), get(), get(), get()) }
}