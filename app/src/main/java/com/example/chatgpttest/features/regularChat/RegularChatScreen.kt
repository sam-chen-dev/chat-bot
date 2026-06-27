package com.example.chatgpttest.features.regularChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.R
import com.example.chatgpttest.features.renderer.MessageContent
import com.example.chatgpttest.utils.ProcessingDialog
import org.koin.androidx.compose.koinViewModel

// Premium Color Palette
private val BackgroundColor = Color(0xFFF1F5F9)
private val SurfaceColor = Color(0xFFFFFFFF)
private val AccentColor = Color(0xFF6366F1)
private val TextHeader = Color(0xFF0F172A)

@Composable
fun RegularChatScreen(onStreamChatClick: () -> Unit, onSettingsClick: () -> Unit) {
    val viewModel: RegularChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inputState = viewModel.inputState
    val outputState = viewModel.outputState

    RegularChatContent(uiState, inputState, outputState, onStreamChatClick, onSettingsClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegularChatContent(
    uiState: RegularChatUiState,
    inputState: TextFieldState,
    outputState: TextFieldState,
    onStreamChatClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    if (uiState.isShowProcessingDialog) {
        ProcessingDialog()
    }

    Scaffold(
        topBar = { PremiumToolbar(onStreamChatClick, onSettingsClick) },
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
            // Workspace Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = AccentColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_workspace),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentColor,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Output Card
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
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    if (outputState.text.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.waiting_for_input),
                                color = Color(0xFF94A3B8),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        val clipboardManager = LocalClipboardManager.current
                        Box(modifier = Modifier.fillMaxSize()) {
                            MessageContent(outputState.text.toString())
                            
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(outputState.text.toString())) },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = stringResource(R.string.copy),
                                    tint = AccentColor
                                )
                            }
                        }
                    }
                }
            }

            // Input Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        state = inputState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp, max = 150.dp),
                        placeholder = { Text(stringResource(R.string.what_can_i_help), color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    
                    Button(
                        onClick = uiState.onSubmitClick,
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.analyze_request),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Rounded.Send, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumToolbar(onStreamChatClick: () -> Unit, onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { 
            Text(
                text = stringResource(R.string.advanced_query),
                fontWeight = FontWeight.ExtraBold,
                color = TextHeader
            ) 
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor,
        ),
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings), tint = TextHeader)
            }

            Surface(
                onClick = onStreamChatClick,
                color = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(40.dp).padding(end = 4.dp),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = stringResource(R.string.switch_to_stream),
                        tint = AccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun RegularChatContentPreview() {
    RegularChatContent(
        uiState = RegularChatUiState(false, {}),
        inputState = TextFieldState(),
        outputState = TextFieldState(),
        onStreamChatClick = {},
        onSettingsClick = {}
    )
}