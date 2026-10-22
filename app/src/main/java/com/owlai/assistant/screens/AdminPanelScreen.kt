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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owlai.assistant.components.NeonGlowCard
import com.owlai.assistant.components.OwlTopBar
import com.owlai.assistant.components.PremiumGradientButton
import com.owlai.assistant.components.StatusPill
import com.owlai.assistant.data.remote.AiService
import com.owlai.assistant.ui.theme.CardDarkNavy
import com.owlai.assistant.ui.theme.CardDarkNavyElevated
import com.owlai.assistant.ui.theme.DeepNavy
import com.owlai.assistant.ui.theme.ErrorRed
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.MidnightBackground
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.SuccessGreen
import com.owlai.assistant.ui.theme.VividPink
import com.owlai.assistant.ui.theme.WarningAmber
import com.owlai.assistant.viewmodel.AdminViewModel

@Composable
fun AdminPanelScreen(
    adminViewModel: AdminViewModel,
    isAdminAuthorized: Boolean,
    onBack: () -> Unit
) {
    val appConfig by adminViewModel.appConfig.collectAsState()
    val allUsers by adminViewModel.allUsers.collectAsState()
    val auditLogs by adminViewModel.auditLogs.collectAsState()
    val isTestingConnection by adminViewModel.isTestingConnection.collectAsState()
    val connectionTestResult by adminViewModel.connectionTestResult.collectAsState()
    val operationMessage by adminViewModel.operationMessage.collectAsState()

    var apiKeyInput by remember { mutableStateOf("") }
    var selectedProviderForTest by remember { mutableStateOf("gemini") }

    val chatProviders = listOf(
        "gemini" to "Google AI Studio (Gemini)",
        "openrouter" to "OpenRouter Gateway"
    )

    val geminiModels = listOf("gemini-2.5-flash", "gemini-2.5-pro", "gemini-1.5-pro")
    val openRouterModels = listOf("anthropic/claude-3.5-sonnet", "openai/gpt-4o", "mistralai/mistral-large")

    val imageProviders = listOf(
        "gemini_imagen" to "Google Imagen 3",
        "pollinations" to "Pollinations AI"
    )

    val videoProviders = listOf(
        "luma" to "Luma Dream Machine (Ray-2)",
        "runway" to "Runway Gen-2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF1F0B26))
                )
            )
            .testTag("admin_panel_screen")
    ) {
        if (!isAdminAuthorized) {
            // Unauthorized shield
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Access Denied: Admin Authorization Required",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your account is not registered in the admin access list or verified as administrator.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onBack) {
                    Text("Return to App")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    OwlTopBar(
                        title = "Owl AI Control Hub",
                        subtitle = "Server-Authorized Admin Panel • Config v${appConfig.version}",
                        onBack = onBack
                    )
                }

                if (operationMessage != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuccessGreen.copy(alpha = 0.15f))
                                .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = operationMessage ?: "",
                                color = SuccessGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // 1. Dashboard Metrics Overview
                item {
                    Text(
                        text = "System Telemetry & Metrics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp, bottom = 6.dp)
                    )
                }

                item {
                    val totalRequests = allUsers.sumOf { it.aiRequestsCount }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            label = "Total Users",
                            value = "${allUsers.size.coerceAtLeast(1)}",
                            accentColor = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = "Active Sessions",
                            value = "Online",
                            accentColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            label = "AI Requests",
                            value = "$totalRequests",
                            accentColor = VividPink,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 2. Maintenance Mode Toggle
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        borderColor = if (appConfig.maintenanceMode) WarningAmber else LavenderPrimary.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Application Maintenance Mode",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "When active, displays maintenance alert to all connected users",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = appConfig.maintenanceMode,
                                onCheckedChange = { adminViewModel.toggleMaintenanceMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = WarningAmber
                                ),
                                modifier = Modifier.testTag("maintenance_mode_switch")
                            )
                        }
                    }
                }

                // 3. AI Provider Management Section
                item {
                    Text(
                        text = "AI Provider Management",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
                    )
                }

                // Chat Provider & Model Configuration
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        borderColor = NeonCyan.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "1. Active Chat Provider & Model",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Provider Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chatProviders.forEach { (key, label) ->
                                    SelectablePill(
                                        text = label,
                                        isSelected = appConfig.activeChatProvider == key,
                                        onSelect = {
                                            val defaultModel = if (key == "gemini") "gemini-2.5-flash" else "anthropic/claude-3.5-sonnet"
                                            adminViewModel.updateChatProvider(key, defaultModel)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Active Model:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val availableModels = if (appConfig.activeChatProvider == "gemini") geminiModels else openRouterModels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableModels.forEach { model ->
                                    SelectablePill(
                                        text = model.substringAfterLast("/"),
                                        isSelected = appConfig.chatModel == model,
                                        onSelect = { adminViewModel.updateChatProvider(appConfig.activeChatProvider, model) }
                                    )
                                }
                            }
                        }
                    }
                }

                // API Key & Provider Credentials Management
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        borderColor = LavenderPrimary.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "2. Provider API Key Management",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LavenderPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Add, rotate, or remove backend provider keys dynamically.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = apiKeyInput,
                                onValueChange = { apiKeyInput = it },
                                placeholder = {
                                    Text(
                                        text = "Paste new Gemini or OpenRouter API Key...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_api_key_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CardDarkNavyElevated,
                                    unfocusedContainerColor = CardDarkNavyElevated,
                                    focusedBorderColor = LavenderPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        adminViewModel.setGeminiApiKey(apiKeyInput)
                                        apiKeyInput = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNavy)
                                ) {
                                    Text("Set Gemini Key", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        adminViewModel.setOpenRouterApiKey(apiKeyInput)
                                        apiKeyInput = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = DeepNavy)
                                ) {
                                    Text("Set OpenRouter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Test Provider Connection
                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        borderColor = SuccessGreen.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "3. Test Provider Latency & Health",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SuccessGreen
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("gemini" to "Gemini", "openrouter" to "OpenRouter", "image" to "Image Engine", "video" to "Video Pipeline").forEach { (id, name) ->
                                    SelectablePill(
                                        text = name,
                                        isSelected = selectedProviderForTest == id,
                                        onSelect = { selectedProviderForTest = id }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { adminViewModel.testProviderConnection(selectedProviderForTest) },
                                enabled = !isTestingConnection,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("test_provider_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CardDarkNavyElevated)
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = NeonCyan,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.NetworkCheck,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Execute Ping Test ($selectedProviderForTest)",
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            if (connectionTestResult != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = connectionTestResult ?: "",
                                    color = NeonCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 4. Feature Toggles & Usage Limits
                item {
                    Text(
                        text = "Feature Toggles & Quotas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
                    )
                }

                item {
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            FeatureToggleRow("AI Chat Assistant", appConfig.chatEnabled) {
                                adminViewModel.toggleFeature("chat", it)
                            }
                            FeatureToggleRow("Math Step Solver", appConfig.mathEnabled) {
                                adminViewModel.toggleFeature("math", it)
                            }
                            FeatureToggleRow("Photo Math Scanner", appConfig.photoMathEnabled) {
                                adminViewModel.toggleFeature("photoMath", it)
                            }
                            FeatureToggleRow("AI Image Creator", appConfig.imageGenEnabled) {
                                adminViewModel.toggleFeature("image", it)
                            }
                            FeatureToggleRow("AI Video Creator", appConfig.videoGenEnabled) {
                                adminViewModel.toggleFeature("video", it)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Daily User Request Limit: ${appConfig.dailyRequestLimit}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Row {
                                    Button(
                                        onClick = { adminViewModel.updateDailyLimit((appConfig.dailyRequestLimit - 10).coerceAtLeast(10)) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CardDarkNavyElevated)
                                    ) { Text("-10", fontSize = 12.sp) }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = { adminViewModel.updateDailyLimit(appConfig.dailyRequestLimit + 25) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CardDarkNavyElevated)
                                    ) { Text("+25", fontSize = 12.sp) }
                                }
                            }
                        }
                    }
                }

                // 5. Audit Logs
                item {
                    Text(
                        text = "Real-Time Audit Trails",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 6.dp)
                    )
                }

                if (auditLogs.isEmpty()) {
                    item {
                        Text(
                            text = "No audit log events recorded yet.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 20.dp)
                        )
                    }
                } else {
                    items(auditLogs.take(8)) { log ->
                        NeonGlowCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp),
                            backgroundColor = CardDarkNavy
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatusPill(text = log.action, color = VividPink)
                                    Text(
                                        text = log.adminId.take(8),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.details,
                                    fontSize = 12.sp,
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

@Composable
private fun AdminStatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDarkNavy)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = accentColor
            )
        }
    }
}

@Composable
private fun FeatureToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = if (checked) Color.White else Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonCyan
            )
        )
    }
}
