package com.example.chatgpttest.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.chatgpttest.R
import com.example.utlikotlin.Text

@Composable
fun ProcessingDialog() {
    Dialog({}) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterStart))
            Text(R.string.processing, modifier = Modifier.align(Alignment.Center))
        }
    }
}