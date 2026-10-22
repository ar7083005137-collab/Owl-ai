package com.owlai.assistant.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owlai.assistant.components.NeonGlowCard
import com.owlai.assistant.components.OwlMascot
import com.owlai.assistant.components.OwlTopBar
import com.owlai.assistant.data.model.ChatMessage
import com.owlai.assistant.ui.theme.CardDarkNavy
import com.owlai.assistant.ui.theme.CardDarkNavyElevated
import com.owlai.assistant.ui.theme.DeepNavy
import com.owlai.assistant.ui.theme.ErrorRed
import com.owlai.assistant.ui.theme.LavenderDeep
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.VividPink
import com.owlai.assistant.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    chatModel: String,
    onBack: () -> Unit
) {
    val chatSessions by chatViewModel.chatSessions.collectAsState()
    val activeChatId by chatViewModel.activeChatId.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val isSending by chatViewModel.isSending.collectAsState()
    val owlExpression by chatViewModel.owlExpression.collectAsState()
    val errorMessage by chatViewModel.errorMessage.collectAsState()

    var inputPrompt by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Ensure there is at least one active chat session
    LaunchedEffect(chatSessions) {
        if (activeChatId == null && chatSessions.isNotEmpty()) {
            chatViewModel.selectChat(chatSessions.first().id)
        } else if (activeChatId == null && chatSessions.isEmpty()) {
            chatViewModel.createNewChat()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF10162B))
                )
            )
            .imePadding()
            .testTag("chat_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with owl mascot expression and session actions
            OwlTopBar(
                title = "Owl AI Chat",
                subtitle = "Model: ${chatModel.substringAfterLast("/")}",
                onBack = onBack,
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { chatViewModel.createNewChat() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LavenderDeep.copy(alpha = 0.4f))
                                .testTag("new_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Chat",
                                tint = NeonCyan
                            )
                        }
                        if (activeChatId != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { activeChatId?.let { chatViewModel.deleteChat(it) } },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(VividPink.copy(alpha = 0.2f))
                                    .testTag("delete_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Chat",
                                    tint = VividPink
                                )
                            }
                        }
                    }
                }
            )

            // Animated Mascot Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDarkNavy.copy(alpha = 0.6f))
                    .border(1.dp, LavenderPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OwlMascot(
                    expression = owlExpression,
                    size = 46.dp,
                    showStatusBadge = false
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Owl Companion",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = owlExpression.statusText,
                        color = owlExpression.badgeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error display
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ErrorRed.copy(alpha = 0.15f))
                        .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Start a fresh discussion with Owl AI! 🦉",
                                color = LavenderPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Quick prompt suggestions
                            PromptSuggestionChip("Explain quantum computing in simple terms") {
                                inputPrompt = it
                            }
                            PromptSuggestionChip("Write a Kotlin extension function for dates") {
                                inputPrompt = it
                            }
                            PromptSuggestionChip("Draft a polite email asking for feedback") {
                                inputPrompt = it
                            }
                        }
                    }
                }

                items(messages, key = { it.id.ifBlank { it.content.hashCode().toString() } }) { msg ->
                    MessageBubble(msg)
                }

                if (isSending) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Owl AI is thinking...",
                                fontSize = 12.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }

            // Bottom Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = {
                        Text(
                            text = "Ask Owl AI anything...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardDarkNavy,
                        unfocusedContainerColor = CardDarkNavy,
                        focusedBorderColor = LavenderPrimary,
                        unfocusedBorderColor = LavenderPrimary.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val text = inputPrompt.trim()
                        if (text.isNotEmpty()) {
                            inputPrompt = ""
                            chatViewModel.sendMessage(text, currentModel = chatModel)
                        }
                    },
                    enabled = inputPrompt.isNotBlank() && !isSending,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputPrompt.isNotBlank() && !isSending) {
                                Brush.linearGradient(listOf(LavenderDeep, LavenderPrimary, NeonCyan))
                            } else {
                                Brush.linearGradient(listOf(Color.DarkGray, Color.Gray))
                            }
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val align = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.linearGradient(listOf(LavenderDeep, LavenderPrimary))
                    } else {
                        Brush.linearGradient(listOf(CardDarkNavy, CardDarkNavyElevated))
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) Color.Transparent else LavenderPrimary.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun PromptSuggestionChip(text: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDarkNavy)
            .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick(text) }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = "✨ $text",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
