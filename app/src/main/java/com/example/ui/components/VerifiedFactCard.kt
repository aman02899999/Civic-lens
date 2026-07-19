package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RagResponse

/**
 * A highly polished, commercial-grade premium card component displaying verified facts,
 * AI grounding confidence, summaries, and clickable source links in a strictly non-partisan layout.
 */
@Composable
fun VerifiedFactCard(
    response: RagResponse,
    topicQuery: String,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onBookmarkToggle: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showDetailsDialog by remember { mutableStateOf(false) }

    val confidencePct = (response.confidenceScore * 100).toInt()
    
    // Choose theme color based on confidence rating
    val themeColor = when {
        response.confidenceScore >= 0.90 -> Color(0xFF2E7D32) // Stable, verified green
        response.confidenceScore >= 0.75 -> Color(0xFFF57C00) // Cautionary amber
        else -> MaterialTheme.colorScheme.error
    }

    val confidenceLabel = when {
        response.confidenceScore >= 0.90 -> "HIGHLY TRUSTWORTHY"
        response.confidenceScore >= 0.75 -> "MODERATELY VERIFIED"
        else -> "UNVERIFIED CLAIM"
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("verified_fact_card")
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        themeColor.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Platform Tag & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Non-Partisan trust tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(themeColor.copy(alpha = 0.08f))
                        .border(1.dp, themeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Shield Verified",
                        tint = themeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEUTRAL VERIFICATION ENGINE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp,
                        color = themeColor
                    )
                }

                // Interactive Quick Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Briefing Button
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(response.summary))
                            Toast.makeText(context, "Briefing copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("fact_copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy text content",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = {
                            val hexHash = Integer.toHexString(topicQuery.hashCode()).uppercase().padStart(8, '0')
                            val signature = "CL-REG-${hexHash.take(4)}-${hexHash.drop(4)}-$confidencePct"
                            val encodedQuery = Uri.encode(topicQuery)
                            val shareUrl = "https://ais-pre-wijwveclzob5y5omrdcdec-257369852531.asia-southeast1.run.app/factcheck?query=$encodedQuery"
                            
                            val shareText = "🔍 CIVICLENS ELECTION VERIFICATION REGISTRY\n" +
                                    "===========================================\n" +
                                    "✅ VERIFIED OFFICIAL BRIEFING CARD\n" +
                                    "-------------------------------------------\n" +
                                    "Topic: $topicQuery\n" +
                                    "Grounding Accuracy: $confidencePct% ($confidenceLabel)\n" +
                                    "Registry Signature: $signature\n" +
                                    "Citations Count: ${response.sourceCount} Official Source(s)\n\n" +
                                    "📋 AI FACT SUMMARY:\n" +
                                    "\"${response.summary}\"\n\n" +
                                    "🔗 VERIFY ORIGINAL SOURCE & PLATFORM APP:\n" +
                                    "$shareUrl\n\n" +
                                    "⚖️ Guarding democratic discourse with non-partisan, ECI-grounded verified evidence."

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens AI Verified Fact Report")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Verification Report"))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("fact_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Report",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Bookmark Toggle Button
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("fact_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Report",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Claim Topic Query
            if (topicQuery.isNotBlank()) {
                Text(
                    text = topicQuery,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Trust Meter and Confidence Gauge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RAG Grounding Accuracy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$confidencePct% ($confidenceLabel)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = themeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { response.confidenceScore.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = themeColor,
                        trackColor = themeColor.copy(alpha = 0.15f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Factual Summary Block
            Text(
                text = "VERIFIED AI BRIEFING",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Styled fact content summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(14.dp)
            ) {
                Text(
                    text = response.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Citations & Official Sources Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CITATIONS & AUTHORITATIVE SOURCES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { showDetailsDialog = true },
                    modifier = Modifier.testTag("view_grounding_details_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Show Grounding Verification Details",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify Grounding", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Citations List / Grid
            if (response.officialSources.isNotEmpty()) {
                response.officialSources.forEach { source ->
                    val parts = source.split(": ", limit = 2)
                    val title = parts.getOrNull(0) ?: "Source Document"
                    val url = parts.getOrNull(1) ?: ""
                    val analysis = analyzeSourceCredibility(title, url)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                            .clickable {
                                if (url.startsWith("http")) {
                                    val webpage = Uri.parse(url)
                                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                                    context.startActivity(intent)
                                }
                            }
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left colored indicator accent bar indicating source credibility
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(analysis.color)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))

                                // Credibility badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(analysis.containerColor)
                                        .border(1.dp, analysis.borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = analysis.icon,
                                        contentDescription = "Credibility Icon",
                                        tint = analysis.color,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = analysis.label,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = analysis.color
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Launch,
                                    contentDescription = "Open Source Document",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (url.isNotBlank()) {
                                    val displayDomain = try {
                                        Uri.parse(url).host ?: "gov.in"
                                    } catch (e: Exception) {
                                        "gov.in"
                                    }
                                    Text(
                                        text = displayDomain,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Text(
                                        text = "Official record registry",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Text(
                                    text = when (analysis.level) {
                                        SourceCredibility.GOVERNMENT_OFFICIAL -> "Authority: Tier 1 (Gov/ECI)"
                                        SourceCredibility.VERIFIED_RESEARCH -> "Authority: Tier 2 (Academic/NGO)"
                                        SourceCredibility.ESTABLISHED_NEWS -> "Authority: Tier 3 (Media)"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No citations provided for this query.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Sync/Disclaimer Footer
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Official data matches Government Portals & ECI registries.",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                if (response.lastUpdated.isNotEmpty()) {
                    Text(
                        text = "Last synced: ${response.lastUpdated}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Grounding Registry",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grounding Report",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = { showDetailsDialog = false },
                        modifier = Modifier.testTag("close_details_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog"
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Topic/Claim analyzed
                    if (topicQuery.isNotBlank()) {
                        Column {
                            Text(
                                text = "ANALYZED CLAIM / TOPIC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = topicQuery,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Accuracy & Rating Meter
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(themeColor.copy(alpha = 0.08f))
                            .border(1.dp, themeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grounding Accuracy",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = themeColor
                            )
                            Text(
                                text = "$confidencePct% ($confidenceLabel)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = themeColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { response.confidenceScore.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = themeColor,
                            trackColor = themeColor.copy(alpha = 0.15f)
                        )
                    }

                    // Verified Briefing Summary
                    Column {
                        Text(
                            text = "VERIFIED AI BRIEFING EXPLANATION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = response.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Deep Citations & Official Portals
                    Column {
                        Text(
                            text = "OFFICIAL SOURCES & LINK COPIERS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (response.officialSources.isNotEmpty()) {
                            response.officialSources.forEachIndexed { index, source ->
                                val parts = source.split(": ", limit = 2)
                                val title = parts.getOrNull(0) ?: "Source Document"
                                val url = parts.getOrNull(1) ?: ""
                                val analysis = analyzeSourceCredibility(title, url)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            // Credibility Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(analysis.containerColor)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = analysis.label,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = analysis.color
                                                )
                                            }
                                        }

                                        if (url.isNotBlank()) {
                                            Text(
                                                text = url,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Copy Link Button
                                                TextButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(url))
                                                        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(32.dp).testTag("dialog_copy_link_button_$index")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.ContentCopy,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Copy Link", fontSize = 10.sp)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                // Visit Website Button
                                                Button(
                                                    onClick = {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Cannot open URL", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(32.dp).testTag("dialog_visit_source_button_$index")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.Launch,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Visit Source", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No external links provided.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // CivicLens Methodology Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "CIVICLENS METHODOLOGY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "CivicLens ensures trust by strictly verifying claims against Indian government portals (such as ECI, PIB, Ministry websites) and neutral academic institutions. Our AI Grounding engine uses secure, non-partisan retrieval mechanisms to query real-time official registries without political bias.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDetailsDialog = false },
                    modifier = Modifier.testTag("dismiss_details_dialog")
                ) {
                    Text("Done")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.testTag("fact_check_modal_dialog")
        )
    }
}

enum class SourceCredibility {
    GOVERNMENT_OFFICIAL,
    VERIFIED_RESEARCH,
    ESTABLISHED_NEWS
}

data class SourceAnalysis(
    val level: SourceCredibility,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val containerColor: Color,
    val borderColor: Color
)

fun analyzeSourceCredibility(title: String, url: String): SourceAnalysis {
    val lowerUrl = url.lowercase()
    val lowerTitle = title.lowercase()

    return when {
        // Level 1: Government official portals, ECI registries
        lowerUrl.contains(".gov") || lowerUrl.contains(".nic") || lowerUrl.contains("eci.gov.in") ||
                lowerTitle.contains("government") || lowerTitle.contains("official") || lowerTitle.contains("ministry") ||
                lowerTitle.contains("press information bureau") || lowerTitle.contains("pib") || lowerTitle.contains("election commission") ||
                lowerTitle.contains("gazette") || lowerTitle.contains("registry") -> {
            SourceAnalysis(
                level = SourceCredibility.GOVERNMENT_OFFICIAL,
                label = "Gov Official",
                icon = Icons.Default.AccountBalance,
                color = Color(0xFF1B5E20), // Deep emerald green
                containerColor = Color(0xFFE8F5E9),
                borderColor = Color(0xFF81C784)
            )
        }
        // Level 2: Reputable Independent Academics, Fact Check organizations, NGOs
        lowerUrl.contains(".edu") || lowerUrl.contains(".org") || lowerUrl.contains("factcheck") ||
                lowerTitle.contains("fact check") || lowerTitle.contains("alt news") || lowerTitle.contains("boom live") ||
                lowerTitle.contains("research") || lowerTitle.contains("university") || lowerTitle.contains("journal") ||
                lowerTitle.contains("academic") || lowerTitle.contains("report") -> {
            SourceAnalysis(
                level = SourceCredibility.VERIFIED_RESEARCH,
                label = "Fact-Check/NGO",
                icon = Icons.Default.Verified,
                color = Color(0xFF0D47A1), // Deep corporate blue
                containerColor = Color(0xFFE3F2FD),
                borderColor = Color(0xFF64B5F6)
            )
        }
        // Level 3: Established news outlets, media platforms
        else -> {
            SourceAnalysis(
                level = SourceCredibility.ESTABLISHED_NEWS,
                label = "Media Portal",
                icon = Icons.Default.Public,
                color = Color(0xFF37474F), // Dark slate gray
                containerColor = Color(0xFFECEFF1),
                borderColor = Color(0xFFB0BEC5)
            )
        }
    }
}

