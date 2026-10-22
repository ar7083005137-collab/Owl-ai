package com.owlai.assistant.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.owlai.assistant.components.NeonGlowCard
import com.owlai.assistant.components.OwlTopBar
import com.owlai.assistant.components.PremiumGradientButton
import com.owlai.assistant.components.StatusPill
import com.owlai.assistant.ui.theme.CardDarkNavy
import com.owlai.assistant.ui.theme.CardDarkNavyElevated
import com.owlai.assistant.ui.theme.DeepNavy
import com.owlai.assistant.ui.theme.ErrorRed
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.PinkAccent
import com.owlai.assistant.ui.theme.VividPink
import com.owlai.assistant.viewmodel.CreationViewModel

@Composable
fun VideoCreatorScreen(
    creationViewModel: CreationViewModel,
    videoProvider: String,
    videoModel: String,
    onBack: () -> Unit
) {
    val creations by creationViewModel.creations.collectAsState()
    val isGenerating by creationViewModel.isGenerating.collectAsState()
    val errorMessage by creationViewModel.errorMessage.collectAsState()

    var promptText by remember { mutableStateOf("") }
    var selectedMotion by remember { mutableStateOf("Cinematic Orbit") }

    val motionPresets = listOf("Cinematic Orbit", "Drone Aerial", "Slow Motion", "Hyperlapse", "Tracking Shot")
    val videoCreations = creations.filter { it.type == "video" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF240E26))
                )
            )
            .testTag("video_creator_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OwlTopBar(
                    title = "AI Video Creator",
                    subtitle = "Engine: $videoProvider ($videoModel)",
                    onBack = onBack
                )
            }

            // Input card
            item {
                NeonGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    borderColor = VividPink.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Video Prompt & Scene Description",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = VividPink
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            placeholder = {
                                Text(
                                    text = "e.g., A golden owl flying through a crystalline aurora borealis...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("video_prompt_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardDarkNavyElevated,
                                unfocusedContainerColor = CardDarkNavyElevated,
                                focusedBorderColor = VividPink,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Camera & Motion Preset",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            motionPresets.take(3).forEach { motion ->
                                SelectablePill(
                                    text = motion,
                                    isSelected = selectedMotion == motion,
                                    onSelect = { selectedMotion = it }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            motionPresets.drop(3).forEach { motion ->
                                SelectablePill(
                                    text = motion,
                                    isSelected = selectedMotion == motion,
                                    onSelect = { selectedMotion = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        PremiumGradientButton(
                            text = "Render Video Scene",
                            onClick = {
                                creationViewModel.generateVideo(
                                    prompt = promptText,
                                    motion = selectedMotion,
                                    aspectRatio = "16:9",
                                    provider = videoProvider,
                                    model = videoModel
                                )
                            },
                            icon = Icons.Default.Movie,
                            isLoading = isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_video_button"),
                            gradient = Brush.horizontalGradient(listOf(VividPink, PinkAccent, LavenderPrimary))
                        )
                    }
                }
            }

            // Error display
            if (errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ErrorRed.copy(alpha = 0.15f))
                            .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = ErrorRed,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Rendering indicator
            if (isGenerating) {
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = VividPink,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Synthesizing cinematic video keyframes with $videoModel...",
                                color = VividPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Video gallery
            if (videoCreations.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Video Productions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                    )
                }

                items(videoCreations) { creation ->
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        borderColor = VividPink.copy(alpha = 0.3f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardDarkNavyElevated)
                            ) {
                                AsyncImage(
                                    model = creation.mediaUrl,
                                    contentDescription = creation.prompt,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Play overlay badge
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .align(Alignment.Center),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play video",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { creationViewModel.deleteCreation(creation.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = VividPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = creation.prompt,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusPill(text = "16:9 • 60 FPS", color = NeonCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusPill(text = creation.model, color = VividPink)
                            }
                        }
                    }
                }
            }
        }
    }
}
