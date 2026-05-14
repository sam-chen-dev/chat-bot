package com.example.chatgpttest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.chatgpttest.navigation.NavDisplay
import com.example.chatgpttest.navigation.RegularChat
import com.example.chatgpttest.ui.theme.ChatGPTTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGPTTestTheme {
                val backStack = rememberNavBackStack(RegularChat)

                NavDisplay(backStack)
            }
        }
    }
}