package com.example.chatgpttest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.chatgpttest.features.regularChat.RegularChatScreen
import com.example.chatgpttest.features.streamChat.StreamChatScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegularChat : NavKey

@Serializable
data object StreamChat : NavKey

@Serializable
data object Settings : NavKey

@Composable
fun EntryProviderScope<NavKey>.RegularChatEntry(backStack: NavBackStack<NavKey>) {
    entry<RegularChat> {
        RegularChatScreen(
            onStreamChatClick = { backStack.add(StreamChat) },
            onSettingsClick = { backStack.add(Settings) }
        )
    }
}

@Composable
fun EntryProviderScope<NavKey>.StreamChatEntry(backStack: NavBackStack<NavKey>) {
    entry<StreamChat> {
        StreamChatScreen(
            onBackClick = { backStack.removeLastOrNull() },
            onSettingsClick = { backStack.add(Settings) }
        )
    }
}

@Composable
fun EntryProviderScope<NavKey>.SettingsEntry(backStack: NavBackStack<NavKey>) {
    entry<Settings> {
        com.example.chatgpttest.features.settings.SettingsScreen(
            onBackClick = { backStack.removeLastOrNull() }
        )
    }
}