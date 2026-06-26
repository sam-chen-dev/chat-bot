package com.example.chatgpttest.features.streamChat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.features.renderer.MessageContent
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.utils.SenderUuid
import org.koin.compose.viewmodel.koinViewModel

// Modern Color Palette
private val BackgroundColor = Color(0xFFF8FAFC)
private val UserBubbleColor = Color(0xFF6366F1)
private val AiBubbleColor = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun StreamChatScreen(onBackClick: () -> Unit) {
    val viewModel: StreamChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inputState = viewModel.inputState

    StreamChatContent(uiState, inputState, onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamChatContent(
    uiState: StreamChatUiState,
    inputState: TextFieldState,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.chatMessages) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = { ModernToolbar(onBackClick, uiState.onNewChatClick) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ChatMessageList(listState, uiState.chatMessages)
            }
            ModernTypingArea(inputState, uiState.onSendClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernToolbar(onBackClick: () -> Unit, onNewChatClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AI Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                /*
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Online Green
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Always Ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                */

            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
        },
        actions = {
            IconButton(onClick = onNewChatClick) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "New Chat", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        ),
        modifier = Modifier.shadow(1.dp)
    )
}

@Composable
private fun ChatMessageList(state: LazyListState, chatMessages: List<ChatMessage>) {
    LazyColumn(
        state = state,
        reverseLayout = true,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(chatMessages) { chatMessage ->
            ModernChatMessageItem(chatMessage)
        }
    }
}

@Composable
private fun ModernChatMessageItem(chatMessage: ChatMessage) {
    val isMe = chatMessage.senderUuid == SenderUuid.ME
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color(0xFFEEF2FF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🤖", fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isMe) 20.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 20.dp
            ),
            color = if (isMe) UserBubbleColor else AiBubbleColor,
            tonalElevation = if (isMe) 0.dp else 2.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                MessageContent(
                    text = chatMessage.text,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (isMe) {
            Spacer(Modifier.width(8.dp))
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = UserBubbleColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun ModernTypingArea(state: TextFieldState, onSendClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    state = state,
                    placeholder = { Text("Ask anything...", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                )
            }

            Spacer(Modifier.width(12.dp))

            Surface(
                onClick = onSendClick,
                shape = CircleShape,
                color = UserBubbleColor,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreamChatContentPreview() {
    StreamChatContent(
        uiState = StreamChatUiState(
            chatMessages = listOf(
                ChatMessage(SenderUuid.GPT, "Hello! How can I assist you with your project today?", 0L),
                ChatMessage(SenderUuid.ME, "I need help with advanced UI design.", 1L)
            ),
            onSendClick = {},
            onNewChatClick = {}
        ),
        inputState = TextFieldState(),
        onBackClick = {},
    )
}
