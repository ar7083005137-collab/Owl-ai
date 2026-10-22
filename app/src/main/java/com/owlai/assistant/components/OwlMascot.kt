package com.owlai.assistant.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owlai.assistant.R
import com.owlai.assistant.ui.theme.LavenderPrimary
import com.owlai.assistant.ui.theme.NeonCyan
import com.owlai.assistant.ui.theme.PurpleGlow
import com.owlai.assistant.ui.theme.VividPink

enum class OwlExpression(val statusText: String, val badgeColor: Color) {
    HAPPY("Ready to Help", NeonCyan),
    THINKING("Analyzing...", LavenderPrimary),
    SOLVING("Calculating Steps...", VividPink),
    CELEBRATING("Problem Solved! 🎉", NeonCyan),
    SLEEPING("Owl Resting (Standby)", Color.Gray)
}

@Composable
fun OwlMascot(
    expression: OwlExpression = OwlExpression.HAPPY,
    size: Dp = 120.dp,
    showStatusBadge: Boolean = true,
    onSecretAdminTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "owl_anim")

    // Subtle bobbing animation
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    // Glowing aura pulse
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .offset(y = bobbingOffset.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onSecretAdminTap?.invoke()
                }
                .testTag("owl_mascot_avatar")
        ) {
            // Neon Glow Halo
            Box(
                modifier = Modifier
                    .size(size * 0.95f)
                    .scale(auraScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                expression.badgeColor.copy(alpha = 0.45f),
                                PurpleGlow.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Mascot Image
            Image(
                painter = painterResource(id = R.drawable.ic_owl_mascot),
                contentDescription = "Owl AI Mascot - ${expression.statusText}",
                modifier = Modifier
                    .size(size * 0.85f)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(NeonCyan, LavenderPrimary, VividPink)
                        ),
                        shape = CircleShape
                    )
            )
        }

        if (showStatusBadge) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = expression.badgeColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = expression.statusText,
                    color = expression.badgeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
