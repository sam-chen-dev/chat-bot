package com.example.chatgpttest.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

@Composable
fun NavDisplay(backStack: NavBackStack<NavKey>) {
    val animationSpec = tween<IntOffset>(500)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = Modifier.navigationBarsPadding(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            RegularChatEntry(backStack)
            StreamChatEntry(backStack)
            SettingsEntry(backStack)
        },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = animationSpec
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = animationSpec
            )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = animationSpec
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = animationSpec
            )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = animationSpec
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = animationSpec
            )
        }
    )
}