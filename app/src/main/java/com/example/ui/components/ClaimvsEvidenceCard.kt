package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbVerifiedNews

/**
 * A highly-polished, responsive side-by-side comparison component showing
 * the initial clashing claim vs the grounded truth/verified evidence.
 *
 * Implements adaptivity (switches to vertical stack on narrow displays and dual-columns on tablets/wider screens).
 */
@Composable
fun ClaimvsEvidenceCard(
    news: DbVerifiedNews,
    modifier: Modifier = Modifier
) {
    val (claim, evidence) = remember(news) { parseClaimAndEvidence(news) }
    
    // Animation for entries on launch/refresh
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(news) {
        animateIn = true
    }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(600),
        label = "fade_in"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alphaAnim)
            .testTag("claim_vs_evidence_card_${news.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Truth Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FactCheck,
                        contentDescription = "Fact Check Status",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Claim Verification Mapping",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Verdict Badge
                val verdictColor = when (news.factCheckVerdict.uppercase()) {
                    "FALSE", "FAKE", "DEBUNKED" -> Color(0xFFC62828)
                    "TRUE", "VERIFIED" -> Color(0xFF2E7D32)
                    "MISLEADING" -> Color(0xFFEF6C00)
                    else -> MaterialTheme.colorScheme.primary
                }
                val verdictLabel = if (news.factCheckVerdict.isNotBlank()) news.factCheckVerdict else "UNVERIFIED"

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(verdictColor.copy(alpha = 0.12f))
                        .border(1.dp, verdictColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (news.factCheckVerdict.uppercase()) {
                            "FALSE", "FAKE", "DEBUNKED" -> Icons.Default.Block
                            "TRUE", "VERIFIED" -> Icons.Default.CheckCircle
                            else -> Icons.AutoMirrored.Filled.HelpOutline
                        },
                        contentDescription = "Verdict Icon",
                        tint = verdictColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = verdictLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = verdictColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Adaptive Layout container (Side-by-Side vs Vertical Stack)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isWide = maxWidth > 480.dp

                if (isWide) {
                    // Side-by-Side row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left Column: Public Claim
                        ClaimPane(
                            claim = claim,
                            modifier = Modifier.weight(1f)
                        )

                        // Central "VS" or "Verified By" Link indicator
                        CenterConnectionIndicator(isVertical = false)

                        // Right Column: Verified Evidence
                        EvidencePane(
                            evidence = evidence,
                            news = news,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Vertical Stack for smaller screens
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClaimPane(claim = claim)
                        CenterConnectionIndicator(isVertical = true)
                        EvidencePane(evidence = evidence, news = news)
                    }
                }
            }
        }
    }
}

@Composable
fun ClaimPane(
    claim: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFEBEE), // Subtle red-pink
                        Color(0xFFFFCDD2)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFFEF5350).copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Viral Claim",
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VIRAL CLAIM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC62828),
                    letterSpacing = 1.sp
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = Color(0xFFC62828).copy(alpha = 0.15f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = claim,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF424242)
                )
            }
        }
    }
}

@Composable
fun EvidencePane(
    evidence: String,
    news: DbVerifiedNews,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9), // Subtle emerald green
                        Color(0xFFC8E6C9)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF66BB6A).copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Grounded Facts",
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "GROUNDED TRUTH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1B5E20),
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = evidence,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B5E20)
            )
            
            if (news.officialSources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Authority: ${news.source}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32).copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CenterConnectionIndicator(isVertical: Boolean) {
    if (isVertical) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "VS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "VS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
            )
        }
    }
}

/**
 * Intelligent parser that extracts claims vs verified evidence statements
 * from seed/mock news inputs.
 */
fun parseClaimAndEvidence(news: DbVerifiedNews): Pair<String, String> {
    val content = news.content
    return when (news.id) {
        "fact_1" -> Pair(
            "WhatsApp messages assert MeitY is providing free 3-month internet recharge to celebrate assembly elections.",
            "The Press Information Bureau (PIB) Fact Check unit verified this claims and declared it false. No such scheme has been launched."
        )
        "fact_2" -> Pair(
            "Social media posts claim grid surcharge of 12% on solar rooftop installs under 2kW under PM Surya Ghar.",
            "The Ministry of Power issued official clarification reaffirming solar connection is fully subsidized and exempt from additional surcharges up to 3kW."
        )
        else -> {
            // General parser heuristics
            val verdictIndex = content.indexOfAny(listOf("verified this claim", "issued an official", "Fact Check unit", "clarification"))
            if (verdictIndex != -1) {
                val claim = content.substring(0, verdictIndex).trim().removeSuffix(".") + "."
                val evidence = content.substring(verdictIndex).trim()
                Pair(claim, evidence)
            } else {
                val sentences = content.split(". ")
                if (sentences.size >= 2) {
                    val claim = sentences.first() + "."
                    val evidence = sentences.drop(1).joinToString(". ")
                    Pair(claim, evidence)
                } else {
                    Pair("Reported viral claim under active RAG verification.", content)
                }
            }
        }
    }
}
