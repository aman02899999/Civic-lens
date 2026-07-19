package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbVerifiedNews

@Composable
fun VisualFactCheckingScorecard(
    factChecks: List<DbVerifiedNews>,
    selectedVerdictFilter: String?,
    onVerdictFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group and count stats
    val totalClaims = factChecks.size
    
    val trueClaimsCount = factChecks.count { it.factCheckVerdict.uppercase() == "TRUE" || it.factCheckVerdict.uppercase() == "VERIFIED" }
    val falseClaimsCount = factChecks.count { it.factCheckVerdict.uppercase() == "FALSE" || it.factCheckVerdict.uppercase() == "FAKE" || it.factCheckVerdict.uppercase() == "DEBUNKED" }
    val misleadingClaimsCount = factChecks.count { it.factCheckVerdict.uppercase() == "MISLEADING" }
    val unverifiedClaimsCount = totalClaims - (trueClaimsCount + falseClaimsCount + misleadingClaimsCount)

    // Average grounding score
    val averageGrounding = if (factChecks.isNotEmpty()) {
        factChecks.map { it.confidenceScore }.average()
    } else {
        0.95
    }

    val truePercentage = if (totalClaims > 0) trueClaimsCount.toFloat() / totalClaims else 0f
    val falsePercentage = if (totalClaims > 0) falseClaimsCount.toFloat() / totalClaims else 0f
    val misleadingPercentage = if (totalClaims > 0) misleadingClaimsCount.toFloat() / totalClaims else 0f
    val unverifiedPercentage = if (totalClaims > 0) unverifiedClaimsCount.toFloat() / totalClaims else 0f

    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(factChecks) {
        animateIn = true
    }

    val animatedTruePct by animateFloatAsState(
        targetValue = if (animateIn) truePercentage else 0f,
        animationSpec = tween(1000),
        label = "true_pct"
    )
    val animatedFalsePct by animateFloatAsState(
        targetValue = if (animateIn) falsePercentage else 0f,
        animationSpec = tween(1000),
        label = "false_pct"
    )
    val animatedMisleadingPct by animateFloatAsState(
        targetValue = if (animateIn) misleadingPercentage else 0f,
        animationSpec = tween(1000),
        label = "misleading_pct"
    )
    val animatedUnverifiedPct by animateFloatAsState(
        targetValue = if (animateIn) unverifiedPercentage else 0f,
        animationSpec = tween(1000),
        label = "unverified_pct"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("visual_fact_checking_scorecard")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Shield Icon + Text + Grounding Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FactCheck,
                        contentDescription = "Scorecard Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "FACT-CHECK SCORECARD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Database Verdict Analytics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Confidence / Grounding level badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Grounding: ${(averageGrounding * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Proportional Distribution Stacked Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verdict Distribution Proportions",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalClaims claims total",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stacked Bar Representation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    val scaleFactor = animatedTruePct + animatedMisleadingPct + animatedFalsePct + animatedUnverifiedPct
                    val finalScale = if (scaleFactor > 0f) scaleFactor else 1f

                    if (animatedTruePct > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedTruePct / finalScale)
                                .background(Color(0xFF2E7D32)) // True (Green)
                        )
                    }
                    if (animatedMisleadingPct > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedMisleadingPct / finalScale)
                                .background(Color(0xFFEF6C00)) // Misleading (Amber)
                        )
                    }
                    if (animatedFalsePct > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedFalsePct / finalScale)
                                .background(Color(0xFFC62828)) // False (Red)
                        )
                    }
                    if (animatedUnverifiedPct > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(animatedUnverifiedPct / finalScale)
                                .background(Color(0xFF78909C)) // Unverified (Slate)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Selector Cards
            Text(
                text = "TAP SEGMENT TO FILTER LIST",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TRUE Segment
                VerdictCard(
                    label = "VERIFIED",
                    count = trueClaimsCount,
                    percentage = (truePercentage * 100).toInt(),
                    color = Color(0xFF2E7D32),
                    isSelected = selectedVerdictFilter == "TRUE",
                    onClick = {
                        if (selectedVerdictFilter == "TRUE") {
                            onVerdictFilterSelected(null)
                        } else {
                            onVerdictFilterSelected("TRUE")
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("scorecard_filter_true")
                )

                // MISLEADING Segment
                VerdictCard(
                    label = "MISLEADING",
                    count = misleadingClaimsCount,
                    percentage = (misleadingPercentage * 100).toInt(),
                    color = Color(0xFFEF6C00),
                    isSelected = selectedVerdictFilter == "MISLEADING",
                    onClick = {
                        if (selectedVerdictFilter == "MISLEADING") {
                            onVerdictFilterSelected(null)
                        } else {
                            onVerdictFilterSelected("MISLEADING")
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("scorecard_filter_misleading")
                )

                // FALSE Segment
                VerdictCard(
                    label = "FALSE/FAKE",
                    count = falseClaimsCount,
                    percentage = (falsePercentage * 100).toInt(),
                    color = Color(0xFFC62828),
                    isSelected = selectedVerdictFilter == "FALSE",
                    onClick = {
                        if (selectedVerdictFilter == "FALSE") {
                            onVerdictFilterSelected(null)
                        } else {
                            onVerdictFilterSelected("FALSE")
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("scorecard_filter_false")
                )
            }

            // Verdict selection feedback / clear row
            if (selectedVerdictFilter != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        .clickable { onVerdictFilterSelected(null) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "Filtered",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filtered by Verdict: ${selectedVerdictFilter.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Clear",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Filter",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerdictCard(
    label: String,
    count: Int,
    percentage: Int,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) color.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$percentage%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
