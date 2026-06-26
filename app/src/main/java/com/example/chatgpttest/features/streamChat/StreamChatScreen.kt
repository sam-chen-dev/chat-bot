package com.example.chatgpttest.features.streamChat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.features.renderer.MessageContent
import com.example.chatgpttest.models.domainModels.ChatMessage
import com.example.chatgpttest.models.domainModels.Conversation
import com.example.chatgpttest.utils.SenderUuid
import kotlinx.coroutines.launch
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.chatMessages) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Color.White
            ) {
                ConversationHistoryDrawer(uiState, scope, drawerState)
            }
        }
    ) {
        Scaffold(
            topBar = {
                ModernToolbar(
                    onBackClick = onBackClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            containerColor = BackgroundColor
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.currentConversationId == null && uiState.chatMessages.isEmpty()) {
                        EmptyChatPlaceholder()
                    } else {
                        ChatMessageList(listState, uiState.chatMessages)
                    }
                }
                ModernTypingArea(inputState, uiState.onSendClick)
            }
        }
    }
}

@Composable
private fun ColumnScope.ConversationHistoryDrawer(
    uiState: StreamChatUiState,
    scope: kotlinx.coroutines.CoroutineScope,
    drawerState: DrawerState
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Chat History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Button(
            onClick = {
                uiState.onNewChatClick()
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("New Chat")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.conversations) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    isSelected = conversation.id == uiState.currentConversationId,
                    onClick = {
                        uiState.onConversationClick(conversation.id)
                        scope.launch { drawerState.close() }
                    },
                    onDelete = { uiState.onDeleteConversationClick(conversation.id) }
                )
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFEEF2FF) else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) UserBubbleColor else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyChatPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🤖", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "How can I help you today?",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernToolbar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary)
            }
        },
        actions = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
            conversations = emptyList(),
            currentConversationId = null,
            onSendClick = {},
            onNewChatClick = {},
            onConversationClick = {},
            onDeleteConversationClick = {}
        ),
        inputState = TextFieldState(),
        onBackClick = {},
    )
}
