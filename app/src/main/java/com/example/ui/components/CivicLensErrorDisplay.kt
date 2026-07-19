package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ErrorType {
    OFFLINE,
    API_FAILURE,
    EMPTY_RESULTS,
    CONFIG_ERROR
}

@Composable
fun CivicLensErrorDisplay(
    errorType: ErrorType,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    onBrowseOffline: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    
    // Smooth infinite breathing/pulse animation for error icons/glow
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleFactor"
    )

    val errorColor = when (errorType) {
        ErrorType.OFFLINE -> Color(0xFFFF9800) // Amber / Warning
        ErrorType.API_FAILURE -> MaterialTheme.colorScheme.error // Crimson / Error
        ErrorType.EMPTY_RESULTS -> MaterialTheme.colorScheme.secondary // Slate / Secondary info
        ErrorType.CONFIG_ERROR -> Color(0xFF9C27B0) // Purple / Config
    }

    val defaultTitle = when (errorType) {
        ErrorType.OFFLINE -> "Network Disconnected"
        ErrorType.API_FAILURE -> "AI Service Temporary Outage"
        ErrorType.EMPTY_RESULTS -> "No Grounded Records Found"
        ErrorType.CONFIG_ERROR -> "API Credentials Required"
    }

    val defaultSubtitle = when (errorType) {
        ErrorType.OFFLINE -> "CivicLens operates offline-first. Your query cannot fetch external real-time data right now, but local databases remain fully active."
        ErrorType.API_FAILURE -> "The remote ECI-grounding model returned a connection error. We have retrieved verified records from our secure offline SQLite database instead."
        ErrorType.EMPTY_RESULTS -> "Our RAG engine searched official sources but found no verified parameters matching your query. Try searching for major political candidates, schemes, or constituencies."
        ErrorType.CONFIG_ERROR -> "The Gemini API integration requires a valid API key configured in AI Studio's secure Secrets environment to access live reasoning."
    }

    val iconVector = when (errorType) {
        ErrorType.OFFLINE -> Icons.Default.WifiOff
        ErrorType.API_FAILURE -> Icons.Default.Info
        ErrorType.EMPTY_RESULTS -> Icons.Default.SearchOff
        ErrorType.CONFIG_ERROR -> Icons.Default.VpnKey
    }

    // Outer Glassmorphic Card Container
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("error_display_component"),
        shape = RoundedCornerShape(24.dp),
        borderWidth = 1.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Visual Canvas/Pulsing Graphic representing error state
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background animated pulse glow rings
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = errorColor.copy(alpha = 0.08f * pulseAlpha),
                        radius = size.minDimension / 1.1f * scaleFactor
                    )
                    drawCircle(
                        color = errorColor.copy(alpha = 0.15f * pulseAlpha),
                        radius = size.minDimension / 1.5f
                    )
                }
                
                // Centered Icon with semantic colors
                Icon(
                    imageVector = iconVector,
                    contentDescription = defaultTitle,
                    tint = errorColor,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Error Title
            Text(
                text = defaultTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Descriptive Subtitle / Diagnostics
            Text(
                text = errorMessage ?: defaultSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Diagnostic Tech Pill
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(errorColor.copy(alpha = 0.08f))
                    .padding(vertical = 4.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = "DIAGNOSTIC CODE: CL_ERR_${errorType.name}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = errorColor,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Triggers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Retry Trigger Button if available
                if (onRetry != null) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = errorColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("retry_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cached,
                                contentDescription = "Retry",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Retry Connection",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Browse Offline Database Button
                if (onBrowseOffline != null) {
                    OutlinedButton(
                        onClick = onBrowseOffline,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                )
                            )
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("offline_browse_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Browse Offline Data",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Browse Offline Data",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
