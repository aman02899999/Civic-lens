package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Premium micro-interaction and loading-state components.
 * Motion follows the project's design guidelines: 150-300ms interaction feedback,
 * skeleton screens for loads over ~300ms, and all animation kept in the UI layer.
 */

/**
 * Animated shimmer placeholder block, used to build skeleton loading screens
 * instead of a bare spinner so layout space is reserved and perceived load is faster.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1200, easing = LinearEasing)),
        label = "shimmerProgress"
    )
    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val highlightColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .clip(shape)
            .drawBehind {
                val bandWidth = size.width
                val startX = -bandWidth + (2 * bandWidth * progress)
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(baseColor, highlightColor, baseColor),
                        start = Offset(startX, 0f),
                        end = Offset(startX + bandWidth, size.height)
                    )
                )
            }
    )
}

/**
 * Skeleton placeholder shaped like the app's news/list cards: a leading thumbnail
 * block beside two text lines. Drop-in replacement for a spinner while a list loads.
 */
@Composable
fun ShimmerListCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp),
                shape = RoundedCornerShape(6.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp),
                shape = RoundedCornerShape(6.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp),
                shape = RoundedCornerShape(6.dp)
            )
        }
    }
}

/**
 * Text that counts up from 0 to [target] when it first appears — used for stat
 * tiles so numbers feel alive instead of static.
 */
@Composable
fun AnimatedCounterText(
    target: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = MaterialTheme.colorScheme.onSurface,
    suffix: String = ""
) {
    var started by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (started) target else 0,
        animationSpec = tween(durationMillis = 900),
        label = "counterValue"
    )
    LaunchedEffect(Unit) { started = true }

    Text(
        text = "$animatedValue$suffix",
        style = style,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

/**
 * Returns the scale factor for a press interaction: springs to [pressedScale]
 * while pressed and back to 1f on release. Apply via graphicsLayer on the caller.
 */
@Composable
fun animatePressScale(isPressed: Boolean, pressedScale: Float = 0.96f): Float {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressScale"
    )
    return scale
}

/**
 * Small pulsing dot used for "live" indicators (e.g. the LIVE TRACKER badge),
 * drawing the eye without being distracting.
 */
@Composable
fun PulsingDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 6.dp
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .drawBehind { drawRect(color.copy(alpha = alpha)) }
    )
}
