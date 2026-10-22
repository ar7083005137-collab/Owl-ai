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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
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
import com.owlai.assistant.ui.theme.LavenderDeep
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.VividPink
import com.owlai.assistant.viewmodel.CreationViewModel

@Composable
fun ImageCreatorScreen(
    creationViewModel: CreationViewModel,
    imageProvider: String,
    imageModel: String,
    onBack: () -> Unit
) {
    val creations by creationViewModel.creations.collectAsState()
    val isGenerating by creationViewModel.isGenerating.collectAsState()
    val lastGeneratedMedia by creationViewModel.lastGeneratedMedia.collectAsState()
    val errorMessage by creationViewModel.errorMessage.collectAsState()

    var promptText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Photorealistic") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") }

    val styles = listOf("Photorealistic", "Cyberpunk", "Anime", "3D Render", "Fantasy", "Minimalist")
    val aspectRatios = listOf("1:1", "9:16", "16:9")

    val imageCreations = creations.filter { it.type == "image" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF1B0F2A))
                )
            )
            .testTag("image_creator_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OwlTopBar(
                    title = "AI Image Creator",
                    subtitle = "Engine: $imageProvider ($imageModel)",
                    onBack = onBack
                )
            }

            // Input card
            item {
                NeonGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    borderColor = LavenderPrimary.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Describe your image",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LavenderPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            placeholder = {
                                Text(
                                    text = "e.g., A cybernetic owl perched on a neon skyscraper in rain...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("image_prompt_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardDarkNavyElevated,
                                unfocusedContainerColor = CardDarkNavyElevated,
                                focusedBorderColor = LavenderPrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Style selector
                        Text(
                            text = "Artistic Style",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            styles.take(3).forEach { style ->
                                SelectablePill(
                                    text = style,
                                    isSelected = selectedStyle == style,
                                    onSelect = { selectedStyle = it }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            styles.drop(3).forEach { style ->
                                SelectablePill(
                                    text = style,
                                    isSelected = selectedStyle == style,
                                    onSelect = { selectedStyle = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Aspect ratio selector
                        Text(
                            text = "Aspect Ratio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            aspectRatios.forEach { ratio ->
                                SelectablePill(
                                    text = ratio,
                                    isSelected = selectedAspectRatio == ratio,
                                    onSelect = { selectedAspectRatio = it }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        PremiumGradientButton(
                            text = "Generate Artwork",
                            onClick = {
                                creationViewModel.generateImage(
                                    prompt = promptText,
                                    style = selectedStyle,
                                    aspectRatio = selectedAspectRatio,
                                    provider = imageProvider,
                                    model = imageModel
                                )
                            },
                            icon = Icons.Default.Palette,
                            isLoading = isGenerating,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_image_button"),
                            gradient = Brush.horizontalGradient(listOf(LavenderDeep, LavenderPrimary, NeonCyan))
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

            // Generation preview
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
                                color = LavenderPrimary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Owl AI is painting pixels with $imageModel...",
                                color = LavenderPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Past Image Creations Gallery
            if (imageCreations.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Generated Gallery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                    )
                }

                items(imageCreations) { creation ->
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        borderColor = LavenderPrimary.copy(alpha = 0.25f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardDarkNavyElevated)
                            ) {
                                AsyncImage(
                                    model = creation.mediaUrl,
                                    contentDescription = creation.prompt,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
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
                                StatusPill(text = creation.aspectRatio, color = NeonCyan)
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusPill(text = creation.model, color = LavenderPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectablePill(
    text: String,
    isSelected: Boolean,
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) LavenderPrimary else CardDarkNavyElevated)
            .border(
                1.dp,
                if (isSelected) NeonCyan else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable { onSelect(text) }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) DeepNavy else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}
