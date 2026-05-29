package com.example.chatgpttest.features.regularChat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatgpttest.R
import com.example.chatgpttest.utils.ProcessingDialog
import com.example.utlikotlin.Button
import com.example.utlikotlin.IconButton
import com.example.utlikotlin.Text
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegularChatScreen(onStreamChatClick: () -> Unit) {
    val viewModel: RegularChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inputState = viewModel.inputState
    val outputState = viewModel.outputState

    RegularChatContent(uiState, inputState, outputState, onStreamChatClick)
}

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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Toolbar(onStreamChatClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutputTextField(outputState)
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputTextField(inputState)

                Spacer(Modifier.height(16.dp))

                Button("Submit", uiState.onSubmitClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(onStreamChatClick: () -> Unit) {
    TopAppBar(
        title = { Text(R.string.regular_chat_title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            IconButton(Icons.Outlined.ChatBubbleOutline, "StreamChat", onStreamChatClick)
        }
    )
}

@Composable
private fun ColumnScope.InputTextField(state: TextFieldState) {
    OutlinedTextField(
        state = state,
        label = { Text("Input") },
        labelPosition = TextFieldLabelPosition.Attached(true),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    )
}

@Composable
private fun OutputTextField(state: TextFieldState) {
    OutlinedTextField(
        state = state,
        label = { Text("Output") },
        labelPosition = TextFieldLabelPosition.Attached(true),
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
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