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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.owlai.assistant.components.MaintenanceBanner
import com.owlai.assistant.components.NeonGlowCard
import com.owlai.assistant.components.OwlExpression
import com.owlai.assistant.components.OwlMascot
import com.owlai.assistant.components.StatusPill
import com.owlai.assistant.data.model.AppConfig
import com.owlai.assistant.data.model.UserProfile
import com.owlai.assistant.ui.theme.CardDarkNavy
import com.owlai.assistant.ui.theme.CardDarkNavyElevated
import com.owlai.assistant.ui.theme.DeepNavy
import com.owlai.assistant.ui.theme.LavenderDeep
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.PinkAccent
import com.owlai.assistant.ui.theme.VividPink

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    appConfig: AppConfig,
    isAdmin: Boolean,
    onNavigateToChat: () -> Unit,
    onNavigateToMath: () -> Unit,
    onNavigateToPhotoMath: () -> Unit,
    onNavigateToImageGen: () -> Unit,
    onNavigateToVideoGen: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    var mascotTapCount by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF11172A))
                )
            )
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Maintenance Banner
            if (appConfig.maintenanceMode) {
                item {
                    MaintenanceBanner()
                }
            }

            // Top Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hello, ${userProfile?.displayName?.split(" ")?.firstOrNull() ?: "Explorer"}! 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Owl AI Smart Hub",
                                style = MaterialTheme.typography.bodySmall,
                                color = LavenderPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusPill(
                                text = appConfig.chatModel.substringAfterLast("/"),
                                color = NeonCyan
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isAdmin) {
                            IconButton(
                                onClick = onNavigateToAdmin,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(VividPink.copy(alpha = 0.2f))
                                    .testTag("admin_panel_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = VividPink
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Profile Avatar
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, LavenderPrimary, CircleShape)
                                .clickable { onNavigateToProfile() }
                                .testTag("home_profile_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!userProfile?.photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = userProfile?.photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Owl Mascot Banner Card
            item {
                NeonGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    backgroundColor = CardDarkNavy,
                    borderColor = LavenderPrimary.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OwlMascot(
                            expression = OwlExpression.HAPPY,
                            size = 80.dp,
                            showStatusBadge = false,
                            onSecretAdminTap = {
                                mascotTapCount++
                                if (mascotTapCount >= 5) {
                                    mascotTapCount = 0
                                    onNavigateToAdmin()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "What can I solve today?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap any smart capability below to begin. I'm trained for deep reasoning and step-by-step answers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // AI Daily usage meter
                            val requests = userProfile?.aiRequestsCount ?: 0L
                            val limit = appConfig.dailyRequestLimit.coerceAtLeast(1L)
                            val progress = (requests.toFloat() / limit.toFloat()).coerceIn(0f, 1f)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Requests: $requests / $limit",
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (progress > 0.85f) VividPink else NeonCyan,
                                trackColor = CardDarkNavyElevated
                            )
                        }
                    }
                }
            }

            // Quick Actions Section Header
            item {
                Text(
                    text = "AI Capabilities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)
                )
            }

            // 1. AI Chat
            item {
                HubFeatureCard(
                    title = "AI Chat Assistant",
                    tagline = "General intelligence, coding, ideas & writing",
                    icon = Icons.Default.AutoAwesome,
                    badge = "Gemini / OpenRouter",
                    accentColor = LavenderPrimary,
                    enabled = appConfig.chatEnabled,
                    onClick = onNavigateToChat,
                    tag = "nav_card_chat"
                )
            }

            // 2. Math Solver
            item {
                HubFeatureCard(
                    title = "Math Solver",
                    tagline = "Step-by-step formulas, algebra, calculus & graphs",
                    icon = Icons.Default.Calculate,
                    badge = "Step Explanations",
                    accentColor = NeonCyan,
                    enabled = appConfig.mathEnabled,
                    onClick = onNavigateToMath,
                    tag = "nav_card_math"
                )
            }

            // 3. Photo Math
            item {
                HubFeatureCard(
                    title = "Photo Math",
                    tagline = "Select textbook or notebook photo to extract and solve",
                    icon = Icons.Default.CameraAlt,
                    badge = "Photo Picker",
                    accentColor = VividPink,
                    enabled = appConfig.photoMathEnabled,
                    onClick = onNavigateToPhotoMath,
                    tag = "nav_card_photo_math"
                )
            }

            // 4. AI Image Creator
            item {
                HubFeatureCard(
                    title = "AI Image Creator",
                    tagline = "Generate high-resolution digital art in multiple aspect ratios",
                    icon = Icons.Default.Palette,
                    badge = "Imagen 3 / SD",
                    accentColor = Color(0xFFFFB74D),
                    enabled = appConfig.imageGenEnabled,
                    onClick = onNavigateToImageGen,
                    tag = "nav_card_image_creator"
                )
            }

            // 5. AI Video Creator
            item {
                HubFeatureCard(
                    title = "AI Video Creator",
                    tagline = "Prompt-to-video scenes with cinematic camera movement",
                    icon = Icons.Default.Movie,
                    badge = "Luma / Runway",
                    accentColor = PinkAccent,
                    enabled = appConfig.videoGenEnabled,
                    onClick = onNavigateToVideoGen,
                    tag = "nav_card_video_creator"
                )
            }
        }
    }
}

@Composable
private fun HubFeatureCard(
    title: String,
    tagline: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    NeonGlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .testTag(tag),
        backgroundColor = CardDarkNavy,
        borderColor = if (enabled) accentColor.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f),
        onClick = if (enabled) onClick else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) accentColor else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) Color.White else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusPill(
                        text = if (enabled) badge else "Disabled",
                        color = if (enabled) accentColor else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
