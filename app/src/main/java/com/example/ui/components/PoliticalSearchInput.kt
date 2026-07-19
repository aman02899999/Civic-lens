package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Suggested claim/topic model
 */
data class InvestigationSuggestion(
    val query: String,
    val type: SuggestionType,
    val shortLabel: String
)

enum class SuggestionType {
    CLAIM, TOPIC, VERIFY
}

/**
 * A highly polished, custom-branded Search Input Component designed for investigating 
 * political topics and election-related claims with CivicLens AI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticalSearchInput(
    onInvestigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    placeholderText: String = "Type a political topic or election claim to investigate..."
) {
    var query by remember { mutableStateOf(initialQuery) }

    val suggestions = listOf(
        InvestigationSuggestion(
            query = "Are electronic voting machines (EVMs) susceptible to Wi-Fi/Bluetooth hacking?",
            type = SuggestionType.CLAIM,
            shortLabel = "EVM Tampering Claim"
        ),
        InvestigationSuggestion(
            query = "What is the official eligibility and benefit structure of PM-KISAN scheme?",
            type = SuggestionType.VERIFY,
            shortLabel = "PM-KISAN Benefits"
        ),
        InvestigationSuggestion(
            query = "Compare candidate affidavits: wealth distribution and pending cases.",
            type = SuggestionType.TOPIC,
            shortLabel = "Candidate Affidavits"
        ),
        InvestigationSuggestion(
            query = "Is voter registration fully digitalized across all constituencies?",
            type = SuggestionType.CLAIM,
            shortLabel = "Digital Voter ID Claim"
        ),
        InvestigationSuggestion(
            query = "Elections budget: tracking advertisement spend vs civic infrastructure.",
            type = SuggestionType.TOPIC,
            shortLabel = "Campaign Spend"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.FactCheck,
                contentDescription = "Verify Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI Claim & Topic Investigator",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Custom Search Text Field
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    text = placeholderText,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { query = "" },
                        modifier = Modifier.testTag("political_search_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search query",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = false,
            maxLines = 3,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("political_search_text_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Suggested Chips Carousel
        Text(
            text = "Suggested Investigations:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("political_search_suggestions_row")
        ) {
            items(suggestions) { suggestion ->
                val (chipBg, chipBorder, chipIcon) = when (suggestion.type) {
                    SuggestionType.CLAIM -> Triple(
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                        Icons.AutoMirrored.Filled.FactCheck
                    )
                    SuggestionType.TOPIC -> Triple(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        Icons.Default.Topic
                    )
                    SuggestionType.VERIFY -> Triple(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        Icons.Default.Search
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(16.dp))
                        .clickable { query = suggestion.query }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("suggestion_chip_${suggestion.shortLabel.replace(" ", "_")}")
                ) {
                    Icon(
                        imageVector = chipIcon,
                        contentDescription = suggestion.type.name,
                        tint = when (suggestion.type) {
                            SuggestionType.CLAIM -> MaterialTheme.colorScheme.error
                            SuggestionType.TOPIC -> MaterialTheme.colorScheme.secondary
                            SuggestionType.VERIFY -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = suggestion.shortLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Trigger Button
        Button(
            onClick = {
                if (query.isNotBlank()) {
                    onInvestigate(query)
                }
            },
            enabled = query.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("political_search_investigate_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Investigate with Grounded AI",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
