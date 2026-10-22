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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
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
fun MathSolverScreen(
    mathViewModel: MathViewModel,
    onBack: () -> Unit
) {
    val currentQuery by mathViewModel.currentQuery.collectAsState()
    val steps by mathViewModel.steps.collectAsState()
    val finalAnswer by mathViewModel.finalAnswer.collectAsState()
    val isSolving by mathViewModel.isSolving.collectAsState()
    val owlExpression by mathViewModel.owlExpression.collectAsState()
    val errorMessage by mathViewModel.errorMessage.collectAsState()
    val solutionHistory by mathViewModel.solutionHistory.collectAsState()

    val mathSymbols = listOf("√", "x²", "π", "∫", "sin", "cos", "(", ")", "+", "−", "×", "÷")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepNavy, MidnightBackground, Color(0xFF0F1A2B))
                )
            )
            .testTag("math_solver_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OwlTopBar(
                    title = "Math Solver",
                    subtitle = "Algebra, Calculus & Geometry Step-by-Step",
                    onBack = onBack
                )
            }

            // Owl Mascot Solving Status
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
                            text = "Smart Mathematical Engine",
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

            // Input card
            item {
                NeonGlowCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    borderColor = NeonCyan.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Enter Problem / Equation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentQuery,
                            onValueChange = { mathViewModel.updateQuery(it) },
                            placeholder = {
                                Text(
                                    text = "e.g., 2x + 5 = 15, or ∫ x^2 dx, or √144",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (currentQuery.isNotEmpty()) {
                                    IconButton(onClick = { mathViewModel.clear() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("math_query_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CardDarkNavyElevated,
                                unfocusedContainerColor = CardDarkNavyElevated,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick symbol palette
                        Text(
                            text = "Quick Math Symbols:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mathSymbols.take(6).forEach { symbol ->
                                MathSymbolChip(symbol) {
                                    mathViewModel.updateQuery(currentQuery + it)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mathSymbols.drop(6).forEach { symbol ->
                                MathSymbolChip(symbol) {
                                    mathViewModel.updateQuery(currentQuery + it)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumGradientButton(
                            text = "Solve Step-by-Step",
                            onClick = { mathViewModel.solve(isPhoto = false) },
                            icon = Icons.Default.Calculate,
                            isLoading = isSolving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("solve_math_button"),
                            gradient = Brush.horizontalGradient(listOf(Color(0xFF00B4D8), NeonCyan, LavenderPrimary))
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

            // Results Section: Steps & Final Answer
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
                                    text = "Step-by-Step Solution Breakdown",
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
                                        color = NeonCyan,
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

            // Past Solutions History
            if (solutionHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Math Solutions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                    )
                }

                items(solutionHistory.take(5)) { sol ->
                    NeonGlowCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        backgroundColor = CardDarkNavy
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = sol.query,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Answer: ${sol.finalAnswer}",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MathSymbolChip(symbol: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardDarkNavy)
            .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .clickable { onClick(symbol) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = NeonCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
