package com.example.chatgpttest.features.regularChat

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.features.renderer.MessageContent
import com.example.chatgpttest.ui.theme.AccentColor
import com.example.chatgpttest.ui.theme.BackgroundColor
import com.example.chatgpttest.ui.theme.SurfaceColor
import com.example.chatgpttest.ui.theme.TextHeader
import com.example.chatgpttest.utils.ProcessingDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegularChatScreen(onStreamChatClick: () -> Unit) {
    val viewModel: RegularChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inputState = viewModel.inputState
    val outputState = viewModel.outputState

    RegularChatContent(uiState, inputState, outputState, onStreamChatClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegularChatContent(
    uiState: RegularChatUiState,
    inputState: TextFieldState,
    outputState: TextFieldState,
    onStreamChatClick: () -> Unit
) {
    if (uiState.isShowProcessingDialog) {
        ProcessingDialog()
    }

    Scaffold(
        topBar = { PremiumToolbar(onStreamChatClick) },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            WorkSpaceHeader()
            OutputCard(outputState)
            InputSection(inputState, uiState.onSubmitClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumToolbar(onStreamChatClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = "Advanced Query", fontWeight = FontWeight.ExtraBold, color = TextHeader) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor,
        ),
        actions = {
            StreamChatIcon(onStreamChatClick)
        }
    )
}

@Composable
private fun StreamChatIcon(onStreamChatClick: () -> Unit) {
    Surface(
        onClick = onStreamChatClick,
        color = Color.White,
        shape = CircleShape,
        modifier = Modifier
            .size(40.dp)
            .padding(end = 4.dp),
        shadowElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Stream Chat",
                tint = AccentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WorkSpaceHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.AutoAwesome, null, tint = AccentColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            "AI WORKSPACE",
            style = MaterialTheme.typography.labelLarge,
            color = AccentColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun ColumnScope.OutputCard(outputState: TextFieldState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (outputState.text.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Waiting for your input...",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                MessageContent(outputState.text.toString())
            }
        }
    }
}

@Composable
private fun ColumnScope.InputSection(inputState: TextFieldState, onSubmitClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceColor,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            InputTextField(inputState)
            AnalyzeRequestButton(onSubmitClick)
        }
    }
}

@Composable
private fun ColumnScope.InputTextField(inputState: TextFieldState) {
    OutlinedTextField(
        state = inputState,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        placeholder = { Text("What can I help you with today?", color = Color(0xFF94A3B8)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun AnalyzeRequestButton(onSubmitClick: () -> Unit) {
    Button(
        onClick = onSubmitClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5)))),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Analyze Request", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.Send, null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegularChatContentPreview() {
    RegularChatContent(
        uiState = RegularChatUiState(false, {}),
        inputState = TextFieldState(),
        outputState = TextFieldState(),
        onStreamChatClick = {}
    )
}