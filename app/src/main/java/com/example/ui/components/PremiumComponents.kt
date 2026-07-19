package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import com.example.data.local.DbCandidate

/**
 * Custom Glassmorphic Card implementing high-contrast visual styling with subtle gradients and metallic borders.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable(onClick = onClick)
    } else {
        modifier.clip(shape)
    }

    Box(
        modifier = cardModifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.60f)
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * Gauge/Progress Bar with animated visual filling for Tracking development metrics like road coverage.
 */
@Composable
fun MetricGauge(
    label: String,
    progressText: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val percentage = progressText.replace("%", "").toFloatOrNull() ?: 50f
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1200),
        label = "MetricGaugeProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = progressText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.7f),
                                color
                            )
                        )
                    )
            )
        }
    }
}

/**
 * styled Seats Distribution horizontal bar chart to represent general Lok Sabha seats in a premium way.
 */
@Composable
fun SeatsChart(
    title: String,
    parties: List<Pair<String, Int>>, // Name and Seat count
    totalSeats: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Multi-segmented bar chart representing distribution
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
        ) {
            val colors = listOf(
                Color(0xFFF15A24), // Saffron (e.g., BJP/NDA style)
                Color(0xFF00B050), // Green (e.g., Opposition/UDF/INC style)
                Color(0xFF0070C0), // Blue (e.g., Regional/Third)
                Color(0xFF7F7F7F)  // Grey (Others)
            )

            parties.forEachIndexed { index, pair ->
                val fraction = pair.second.toFloat() / totalSeats.toFloat()
                if (fraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(fraction)
                            .background(colors.getOrElse(index) { colors.last() })
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chart Legends
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val colors = listOf(Color(0xFFF15A24), Color(0xFF00B050), Color(0xFF0070C0), Color(0xFF7F7F7F))
            parties.forEachIndexed { index, pair ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.getOrElse(index) { colors.last() })
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${pair.first}: ${pair.second}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Fact Check Verdict Badges with beautiful, clean indicators.
 */
@Composable
fun FactCheckBadge(
    verdict: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (verdict.uppercase()) {
        "TRUE" -> Quadruple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle,
            "VERIFIED TRUE"
        )
        "FALSE" -> Quadruple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            Icons.Default.Error,
            "FALSE / FAKE"
        )
        "MISLEADING" -> Quadruple(
            Color(0xFFFFF3E0),
            Color(0xFFEF6C00),
            Icons.Default.Warning,
            "MISLEADING"
        )
        else -> Quadruple(
            Color(0xFFECEFF1),
            Color(0xFF37474F),
            Icons.Default.Info,
            verdict
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

/**
 * Detailed credibility levels based on source and authority parameters.
 */
enum class SourceCredibilityLevel {
    TIER_1_GOVERNMENT, // Gov / ECI Portals
    TIER_2_FACT_CHECK,  // PIB / Reputable NGOs
    TIER_3_ESTABLISHED_NEWS // Established media
}

/**
 * Visual tags to represent the credibility and source of each election fact presented in the app.
 */
@Composable
fun FactCredibilityTagSet(
    source: String,
    confidenceScore: Double,
    isFactCheck: Boolean,
    modifier: Modifier = Modifier
) {
    val lowerSource = source.lowercase()
    
    // Determine Credibility Level & Source Label
    val (credibilityLevel, sourceLabel, sourceIcon, sourceColor, sourceBg) = when {
        lowerSource.contains("gov") || lowerSource.contains("eci") || lowerSource.contains("election commission") || lowerSource.contains("pib") || lowerSource.contains("press information") || lowerSource.contains("ministry") -> {
            listOf(
                SourceCredibilityLevel.TIER_1_GOVERNMENT,
                "GOVERNMENT COGNIZANT",
                Icons.Default.CheckCircle,
                Color(0xFF1B5E20), // Deep emerald green
                Color(0xFFE8F5E9)
            )
        }
        lowerSource.contains("fact") || lowerSource.contains("ngo") || lowerSource.contains("alt news") || lowerSource.contains("boom") || lowerSource.contains("independent") || isFactCheck -> {
            listOf(
                SourceCredibilityLevel.TIER_2_FACT_CHECK,
                "TRUSTED FACT-CHECK",
                Icons.Default.CheckCircle,
                Color(0xFF0D47A1), // Deep corporate blue
                Color(0xFFE3F2FD)
            )
        }
        else -> {
            listOf(
                SourceCredibilityLevel.TIER_3_ESTABLISHED_NEWS,
                "ESTABLISHED MEDIA",
                Icons.Default.Info,
                Color(0xFF37474F), // Slate gray
                Color(0xFFECEFF1)
            )
        }
    }

    // Determine Grounding Score / Trust rating color
    val confidencePct = (confidenceScore * 100).toInt()
    val (trustLabel, trustColor, trustBg) = when {
        confidenceScore >= 0.90 -> Triple("90%+ RELIABILITY", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        confidenceScore >= 0.75 -> Triple("75%+ RELIABILITY", Color(0xFFEF6C00), Color(0xFFFFF3E0))
        else -> Triple("LOW RELIABILITY", Color(0xFFC62828), Color(0xFFFFEBEE))
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Source Credibility Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(sourceBg as Color)
                .border(0.5.dp, (sourceColor as Color).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = sourceIcon as ImageVector,
                contentDescription = null,
                tint = sourceColor as Color,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = sourceLabel as String,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                color = sourceColor as Color,
                letterSpacing = 0.2.sp
            )
        }

        // 2. Trust index score badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(trustBg)
                .border(0.5.dp, trustColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(trustColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$confidencePct% TRUST INDEX",
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                color = trustColor,
                letterSpacing = 0.2.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class PolicyStance(
    val areaName: String,
    val icon: ImageVector,
    val stance1: String,
    val stance2: String,
    val tag1: String,
    val tag2: String
)

private class CandidateStances(
    val economic: String,
    val economicTag: String,
    val welfare: String,
    val welfareTag: String,
    val infra: String,
    val infraTag: String,
    val education: String,
    val educationTag: String
)

private fun getStancesForParty(partyId: String, partyName: String): CandidateStances {
    val id = partyId.lowercase()
    val name = partyName.lowercase()
    return when {
        id.contains("bjp") || name.contains("bjp") || name.contains("bharatiya janata") -> {
            CandidateStances(
                economic = "Atmanirbhar Bharat (Self-Reliant India): Focuses on production-linked incentives (PLI), self-reliant local manufacturing, expanding Retail Digital Public Infrastructure, and Ease of Doing Business.",
                economicTag = "PRODUCTION-LED",
                welfare = "Direct Benefit Transfers: Focuses on PMJDY zero-balance banking, PM-KISAN direct cash transfers (₹6,000/yr), PM-JAY medical insurance (₹5 Lakh), and subsidized piped water systems.",
                welfareTag = "TARGETED AID",
                infra = "Mega-Infrastructure Master Plan: PM Gati Shakti, high-speed rail corridors, national highway expansion, and integrating 100GW+ of renewable solar power into the national grid.",
                infraTag = "MEGA-INFRA",
                education = "National Education Policy (NEP 2020): Introducing flexible multi-disciplinary degrees, localized vocational training, and active youth skilling under PM Kaushal Vikas Yojana.",
                educationTag = "NEP SKILLING"
            )
        }
        id.contains("inc") || name.contains("inc") || name.contains("congress") || name.contains("indian national") -> {
            CandidateStances(
                economic = "NYAY Economic Model: Emphasizes demand-driven growth by expanding purchasing power of poorest citizens, legally guaranteeing agricultural Minimum Support Prices (MSP), and lowering MSME taxes.",
                economicTag = "DEMAND-DRIVEN",
                welfare = "Legally Guaranteed Social Safety Nets: Right to Health legislation, direct income guarantee (₹72,000/yr for bottom 20%), expansion of MGNREGA wages, and social representation quotas.",
                welfareTag = "GUARANTEED RIGHTS",
                infra = "Decentralized Green Development: Strengthening village panchayat infrastructures, localized solar energy cooperatives for farming communities, and expanding secondary rural roads.",
                infraTag = "RURAL CO-OPS",
                education = "Pehli Naukri Pakki Apprenticeship: Ensuring legal right to a paid one-year apprenticeship for all college graduates, education loan waiver plans, and reform of examination patterns.",
                educationTag = "APPRENTICESHIP"
            )
        }
        id.contains("aap") || name.contains("aap") || name.contains("aam aadmi") -> {
            CandidateStances(
                economic = "Anti-Inspector-Raj Business Model: Simplifying trade licenses, supporting independent retailers, and increasing treasury compliance through absolute transparency in civic governance.",
                economicTag = "COMPLIANCE-FIRST",
                welfare = "Universal Basic Services Model: Free high-quality local healthcare via Mohalla Clinics, up to 200 units of free household electricity, free drinking water, and monthly cash stipends for women.",
                welfareTag = "UNIVERSAL BASIC",
                infra = "Local Urban Utility Network: Sub-urban transit corridors, expansion of battery swapping stations, and bringing high-speed sewage and water pipeline systems directly to unauthorized colonies.",
                infraTag = "LOCAL UTILITIES",
                education = "State-Funded Education Revolution: Complete rebuilding of public school classrooms, creating specialized skill boards, global teacher exchange programs, and keeping schooling 100% free.",
                educationTag = "STATE SCHOOLS"
            )
        }
        else -> {
            CandidateStances(
                economic = "Sustained Regional Growth: Combining public-private partnerships with specific incentives for local small and medium enterprise clusters to generate employment.",
                economicTag = "REGIONAL COMMERCE",
                welfare = "Targeted Public Support: Expanding coverage of subsidized regional essentials, state-sponsored healthcare clinics, and pension support for senior citizens and laborers.",
                welfareTag = "CITIZEN WELFARE",
                infra = "Utility Corridor Integration: Modernizing inter-city bus terminals, upgrading district roads, and extending power grid outreach to remote agrarian regions.",
                infraTag = "DISTRICT CORRIDORS",
                education = "Skill-First Vocational Training: Upgrading district Industrial Training Institutes (ITIs) and offering free tech-literacy camps to local schools.",
                educationTag = "VOCATIONAL SKILLS"
            )
        }
    }
}

@Composable
fun CandidatePlatformComparisonCard(
    candidate1: DbCandidate,
    candidate2: DbCandidate,
    modifier: Modifier = Modifier
) {
    val stances = remember(candidate1, candidate2) {
        val p1 = getStancesForParty(candidate1.partyId, candidate1.partyName)
        val p2 = getStancesForParty(candidate2.partyId, candidate2.partyName)
        listOf(
            PolicyStance("Economic & Industrial Vision", Icons.Default.AccountBalance, p1.economic, p2.economic, p1.economicTag, p2.economicTag),
            PolicyStance("Social Security & Welfare", Icons.Default.VolunteerActivism, p1.welfare, p2.welfare, p1.welfareTag, p2.welfareTag),
            PolicyStance("Infrastructure & Energy", Icons.Default.Map, p1.infra, p2.infra, p1.infraTag, p2.infraTag),
            PolicyStance("Education & Youth Skills", Icons.Default.School, p1.education, p2.education, p1.educationTag, p2.educationTag)
        )
    }

    GlassCard(
        modifier = modifier.testTag("candidate_platform_comparison_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Candidate 1 vs Candidate 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Candidate 1 Header Brief
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (candidate1.photoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = candidate1.photoUrl,
                                contentDescription = "${candidate1.name} Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = candidate1.name.take(2).uppercase(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = candidate1.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = candidate1.partyName.substringBefore("(").trim(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // VS badge
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "VS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Candidate 2 Header Brief
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = candidate2.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = candidate2.partyName.substringBefore("(").trim(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (candidate2.photoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = candidate2.photoUrl,
                                contentDescription = "${candidate2.name} Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = candidate2.name.take(2).uppercase(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Dynamic Policy Stances Section
            stances.forEachIndexed { index, stance ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Category Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = stance.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stance.areaName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Side-by-Side Platforms content
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Candidate 1 Side
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stance.tag1,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stance.stance1,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }

                        // Thin vertical divider line
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .align(Alignment.CenterVertically)
                                .height(96.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        )

                        // Candidate 2 Side
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = stance.tag2,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stance.stance2,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    if (index < stances.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
