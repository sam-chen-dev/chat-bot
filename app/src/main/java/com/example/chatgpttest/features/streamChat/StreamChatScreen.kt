package com.example.chatgpttest.features.streamChat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.R
import com.example.chatgpttest.models.ChatMessage
import com.example.chatgpttest.utils.SenderUuid
import com.example.utlikotlin.IconButton
import com.example.utlikotlin.Text
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StreamChatScreen(onBackClick: () -> Unit) {
    val viewModel: StreamChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle(emptyList())
    val inputState = viewModel.inputState
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.chatMessages.collectLatest {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar(onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            ChatMessageList(listState, chatMessages)

            HorizontalDivider()

            TypingArea(inputState, uiState.onSendClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(R.string.stream_chat_title) },
        navigationIcon = { IconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", onBackClick) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun ColumnScope.ChatMessageList(state: LazyListState, chatMessages: List<ChatMessage>) {
    LazyColumn(
        state = state,
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    ) {
        items(chatMessages) { chatMessage ->
            ChatMessageListItem(chatMessage)
        }
    }
}

@Composable
private fun ChatMessageListItem(chatMessage: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (chatMessage.senderUuid == SenderUuid.ME) {
            Spacer(Modifier.weight(1F))
            TextView(chatMessage.text, Alignment.End)
        } else {
            TextView(chatMessage.text, Alignment.Start)
        }
    }
}

@Composable
private fun TextView(text: String, alignment: Alignment.Horizontal) {
    Text(
        text,
        fontSize = 15.sp,
        modifier = Modifier
            .fillMaxWidth(0.8F)
            .wrapContentWidth(alignment)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun TypingArea(state: TextFieldState, onSendClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputTextField(state)

        Spacer(Modifier.width(16.dp))

        IconButton(Icons.AutoMirrored.Filled.Send, "Send", onSendClick)
    }
}

@Composable
private fun RowScope.InputTextField(state: TextFieldState) {
    OutlinedTextField(
        state = state,
        placeholder = { Text("Enter here") },
        modifier = Modifier.weight(1F)
    )
}