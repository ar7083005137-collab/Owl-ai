package com.owlai.assistant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.owlai.assistant.navigation.OwlNavHost
import com.owlai.assistant.ui.theme.OwlAITheme

@Composable
fun OwlAIApp() {
    OwlAITheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OwlNavHost()
        }
    }
}
