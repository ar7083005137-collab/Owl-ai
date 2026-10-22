package com.owlai.assistant.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owlai.assistant.components.NeonGlowCard
import com.owlai.assistant.components.OwlMascot
import com.owlai.assistant.components.OwlTopBar
import com.owlai.assistant.components.PremiumGradientButton
import com.owlai.assistant.ui.theme.CardDarkNavy
import com.owlai.assistant.ui.theme.CardDarkNavyElevated
import com.owlai.assistant.ui.theme.DeepNavy
import com.owlai.assistant.ui.theme.ErrorRed
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.SuccessGreen
import com.owlai.assistant.ui.theme.VividPink
import com.owlai.assistant.viewmodel.MathViewModel

@Composable
fun PhotoMathScreen(
    mathViewModel: MathViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedBitmap by mathViewModel.selectedBitmap.collectAsState()
    val steps by mathViewModel.steps.collectAsState()
    val finalAnswer by mathViewModel.finalAnswer.collectAsState()
    val isSolving by mathViewModel.isSolving.collectAsState()
    val owlExpression by mathViewModel.owlExpression.collectAsState()
    val errorMessage by mathViewModel.errorMessage.collectAsState()

    // Android Photo Picker launcher (zero permissions required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    mathViewModel.setSelectedBitmap(bitmap)
                }
            } catch (e: Exception) {
                // handle image read error
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF160D26))
                )
            )
            .testTag("photo_math_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OwlTopBar(
                    title = "Photo Math",
                    subtitle = "Scan and extract handwritten equations",
                    onBack = onBack
                )
            }

            // Mascot Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OwlMascot(
                        expression = owlExpression,
                        size = 64.dp,
                        showStatusBadge = false
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Visual Equation Recognition",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = owlExpression.statusText,
                            color = owlExpression.badgeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Photo Picker Card
            item {
                NeonGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    borderColor = VividPink.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CardDarkNavyElevated)
                            ) {
                                Image(
                                    bitmap = selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Selected Math Image",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { mathViewModel.setSelectedBitmap(null) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CardDarkNavyElevated)
                                    .border(
                                        1.5.dp,
                                        Brush.linearGradient(listOf(VividPink, LavenderPrimary)),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("pick_photo_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Add Photo",
                                        tint = VividPink,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Choose Photo from Gallery",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Snap or pick any formula or problem page",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumGradientButton(
                            text = if (selectedBitmap != null) "Analyze & Solve Equation" else "Select Math Image",
                            onClick = {
                                if (selectedBitmap != null) {
                                    mathViewModel.solve(isPhoto = true)
                                } else {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            },
                            icon = Icons.Default.CameraAlt,
                            isLoading = isSolving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("solve_photo_button"),
                            gradient = Brush.horizontalGradient(listOf(VividPink, LavenderPrimary, NeonCyan))
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

            // Results Section
            if (finalAnswer != null || steps.isNotEmpty()) {
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        borderColor = SuccessGreen.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Extracted Math Solution",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            steps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        fontWeight = FontWeight.Bold,
                                        color = VividPink,
                                        fontSize = 13.sp,
                                        modifier = Modifier.width(24.dp)
                                    )
                                    Text(
                                        text = step,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            if (finalAnswer != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SuccessGreen.copy(alpha = 0.15f))
                                        .border(1.dp, SuccessGreen, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "FINAL RESULT",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = SuccessGreen
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = finalAnswer ?: "",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
