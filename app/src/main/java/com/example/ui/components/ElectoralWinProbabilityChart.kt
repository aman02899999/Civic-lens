package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Model for Win Probability & Sentiment Projection
data class ElectoralProjection(
    val candidateId: String,
    val candidateName: String,
    val partyName: String,
    val partyCode: String, // BJP, INC, AAP, OTH
    val winProbability: Float, // 0.0f to 1.0f (e.g. 0.58f)
    val webTractionIndex: Int, // 1 to 100
    val newsSentimentPositive: Float, // 0.0f to 1.0f
    val newsSentimentNeutral: Float,
    val newsSentimentNegative: Float,
    val publicPulseOptimism: Float, // 0.0f to 1.0f
    val publicPulseKeyThought: String, // e.g. "Appreciates infrastructure investments"
    val publicPulseConcern: String, // e.g. "Concerned about rural unemployment"
    val dataGroundingSummary: String, // Web and news background analytics
    val partyColor: Color
)

@Composable
fun ElectoralWinProbabilityChart(
    modifier: Modifier = Modifier
) {
    // 1. Election Type State & Options
    var selectedElectionType by remember { mutableStateOf("LOK_SABHA") } // "LOK_SABHA", "VIDHAN_SABHA", "MUNICIPAL"
    
    // 2. Party Filter State
    var selectedPartyFilter by remember { mutableStateOf("ALL") } // "ALL", "BJP", "INC", "AAP", "OTH"

    // 3. Selection state for detailed look at a candidate
    var selectedCandidateId by remember { mutableStateOf("narendra_modi") }

    // Seed Projections depending on the Election Type
    val projectionsMap = remember {
        mapOf(
            "LOK_SABHA" to listOf(
                ElectoralProjection(
                    candidateId = "narendra_modi",
                    candidateName = "Narendra Modi",
                    partyName = "Bharatiya Janata Party",
                    partyCode = "BJP",
                    winProbability = 0.56f,
                    webTractionIndex = 92,
                    newsSentimentPositive = 0.60f,
                    newsSentimentNeutral = 0.25f,
                    newsSentimentNegative = 0.15f,
                    publicPulseOptimism = 0.74f,
                    publicPulseKeyThought = "Highly optimistic regarding macroeconomic stability and national digital public infrastructure.",
                    publicPulseConcern = "Voters express ongoing friction regarding localized food inflation and job creation speed.",
                    dataGroundingSummary = "Aggregates 14,000+ national web articles & social listening indicators. High search volumes across Northern & Western states.",
                    partyColor = Color(0xFFF15A24) // BJP Orange
                ),
                ElectoralProjection(
                    candidateId = "rahul_gandhi",
                    candidateName = "Rahul Gandhi",
                    partyName = "Indian National Congress",
                    partyCode = "INC",
                    winProbability = 0.32f,
                    webTractionIndex = 84,
                    newsSentimentPositive = 0.52f,
                    newsSentimentNeutral = 0.28f,
                    newsSentimentNegative = 0.20f,
                    publicPulseOptimism = 0.65f,
                    publicPulseKeyThought = "Widespread resonance with social justice themes, minimum support price guarantees, and caste census discourse.",
                    publicPulseConcern = "Skepticism remains over party structural delivery and coalition co-ordination on policy execution.",
                    dataGroundingSummary = "Derived from 9,500+ digital press articles and town-hall feedback. Surged in search activity across Southern states.",
                    partyColor = Color(0xFF0070C0) // INC Blue
                ),
                ElectoralProjection(
                    candidateId = "arvind_kejriwal",
                    candidateName = "Arvind Kejriwal",
                    partyName = "Aam Aadmi Party",
                    partyCode = "AAP",
                    winProbability = 0.08f,
                    webTractionIndex = 75,
                    newsSentimentPositive = 0.44f,
                    newsSentimentNeutral = 0.30f,
                    newsSentimentNegative = 0.26f,
                    publicPulseOptimism = 0.58f,
                    publicPulseKeyThought = "Exceptional public ratings on local welfare policies, subsidized electricity, and community-level healthcare.",
                    publicPulseConcern = "National traction is limited beyond specific strongholds like Delhi and Punjab due to limited local candidate bases.",
                    dataGroundingSummary = "Based on local municipal reports, northern regional daily papers, and welfare performance metadata indices.",
                    partyColor = Color(0xFF00B050) // AAP Green
                ),
                ElectoralProjection(
                    candidateId = "others",
                    candidateName = "Others / Coalition",
                    partyName = "Regional Parties & Independents",
                    partyCode = "OTH",
                    winProbability = 0.04f,
                    webTractionIndex = 60,
                    newsSentimentPositive = 0.40f,
                    newsSentimentNeutral = 0.42f,
                    newsSentimentNegative = 0.18f,
                    publicPulseOptimism = 0.50f,
                    publicPulseKeyThought = "Decisive influence of strong regional leaders in states like West Bengal, Tamil Nadu, and Andhra Pradesh.",
                    publicPulseConcern = "Concerns over policy alignment consistency and leadership clarity in a broader coalition.",
                    dataGroundingSummary = "Aggregated data from 20+ regional parties across federal states with high localized popularity.",
                    partyColor = Color(0xFF78909C) // Slate Gray
                )
            ),
            "VIDHAN_SABHA" to listOf(
                ElectoralProjection(
                    candidateId = "narendra_modi",
                    candidateName = "BJP State Alliance",
                    partyName = "Bharatiya Janata Party",
                    partyCode = "BJP",
                    winProbability = 0.48f,
                    webTractionIndex = 86,
                    newsSentimentPositive = 0.55f,
                    newsSentimentNeutral = 0.28f,
                    newsSentimentNegative = 0.17f,
                    publicPulseOptimism = 0.68f,
                    publicPulseKeyThought = "Enjoys 'Double-Engine Growth' development appeal across BJP-ruled state assemblies.",
                    publicPulseConcern = "Local anti-incumbency issues against specific MLAs and regional candidates often dilute central leadership popularity.",
                    dataGroundingSummary = "Aggregating regional vernacular newspapers and localized sentiment indexes.",
                    partyColor = Color(0xFFF15A24)
                ),
                ElectoralProjection(
                    candidateId = "rahul_gandhi",
                    candidateName = "Congress State Alliance",
                    partyName = "Indian National Congress",
                    partyCode = "INC",
                    winProbability = 0.36f,
                    webTractionIndex = 82,
                    newsSentimentPositive = 0.58f,
                    newsSentimentNeutral = 0.26f,
                    newsSentimentNegative = 0.16f,
                    publicPulseOptimism = 0.70f,
                    publicPulseKeyThought = "Strong traction on local guarantees, farmer debt relief schemes, and local governance promises.",
                    publicPulseConcern = "Internal factionalism within state units sometimes hinders voter mobilisation.",
                    dataGroundingSummary = "Derived from local poll thoughts, regional feedback, and state assembly seat analysis.",
                    partyColor = Color(0xFF0070C0)
                ),
                ElectoralProjection(
                    candidateId = "arvind_kejriwal",
                    candidateName = "AAP State Alliance",
                    partyName = "Aam Aadmi Party",
                    partyCode = "AAP",
                    winProbability = 0.11f,
                    webTractionIndex = 79,
                    newsSentimentPositive = 0.50f,
                    newsSentimentNeutral = 0.30f,
                    newsSentimentNegative = 0.20f,
                    publicPulseOptimism = 0.64f,
                    publicPulseKeyThought = "High state-level performance marks on primary education, schools of eminence, and water/power subsidy cards.",
                    publicPulseConcern = "Limited administrative reach and organizational framework in non-traditional states.",
                    dataGroundingSummary = "Focused on Delhi, Punjab, Haryana and Gujarat state political developments.",
                    partyColor = Color(0xFF00B050)
                ),
                ElectoralProjection(
                    candidateId = "others",
                    candidateName = "Regional Giants",
                    partyName = "Regional Parties & Independents",
                    partyCode = "OTH",
                    winProbability = 0.05f,
                    webTractionIndex = 70,
                    newsSentimentPositive = 0.45f,
                    newsSentimentNeutral = 0.38f,
                    newsSentimentNegative = 0.17f,
                    publicPulseOptimism = 0.60f,
                    publicPulseKeyThought = "Voters strongly favor regional parties for protecting federal interests and unique state cultural priorities.",
                    publicPulseConcern = "Fragmented mandates might lead to temporary or unstable coalition formations.",
                    dataGroundingSummary = "Synthesized across states with high regional party dominance.",
                    partyColor = Color(0xFF78909C)
                )
            ),
            "MUNICIPAL" to listOf(
                ElectoralProjection(
                    candidateId = "narendra_modi",
                    candidateName = "BJP Local Bodies",
                    partyName = "Bharatiya Janata Party",
                    partyCode = "BJP",
                    winProbability = 0.38f,
                    webTractionIndex = 74,
                    newsSentimentPositive = 0.48f,
                    newsSentimentNeutral = 0.32f,
                    newsSentimentNegative = 0.20f,
                    publicPulseOptimism = 0.60f,
                    publicPulseKeyThought = "Supported heavily on urban cleanliness drives, smart city sewer pipelines, and central civic grants.",
                    publicPulseConcern = "Local corporator availability and accountability issues on basic pothole repairs.",
                    dataGroundingSummary = "Analyzes municipal election results, smart city dashboards, and urban ward reports.",
                    partyColor = Color(0xFFF15A24)
                ),
                ElectoralProjection(
                    candidateId = "rahul_gandhi",
                    candidateName = "INC Local Bodies",
                    partyName = "Indian National Congress",
                    partyCode = "INC",
                    winProbability = 0.34f,
                    webTractionIndex = 72,
                    newsSentimentPositive = 0.46f,
                    newsSentimentNeutral = 0.34f,
                    newsSentimentNegative = 0.20f,
                    publicPulseOptimism = 0.58f,
                    publicPulseKeyThought = "Sustained support in semi-urban wards on local community healthcare and daily wage labor schemes.",
                    publicPulseConcern = "Requires higher digital and grassroots campaign intensity to capture tier-1 metropolitan wards.",
                    dataGroundingSummary = "Sourced from tier-2 and tier-3 city councils, rural panchayat feedback metrics.",
                    partyColor = Color(0xFF0070C0)
                ),
                ElectoralProjection(
                    candidateId = "arvind_kejriwal",
                    candidateName = "AAP Local Bodies",
                    partyName = "Aam Aadmi Party",
                    partyCode = "AAP",
                    winProbability = 0.16f,
                    webTractionIndex = 83,
                    newsSentimentPositive = 0.58f,
                    newsSentimentNeutral = 0.24f,
                    newsSentimentNegative = 0.18f,
                    publicPulseOptimism = 0.72f,
                    publicPulseKeyThought = "Acclaimed widely for solving hyper-local ward-level issues, community sanitation, and free water access.",
                    publicPulseConcern = "Urban-centric local body strategies limit performance in agricultural deep-rural panchayats.",
                    dataGroundingSummary = "Extracted from Municipal Corporation of Delhi (MCD) audits and urban body records.",
                    partyColor = Color(0xFF00B050)
                ),
                ElectoralProjection(
                    candidateId = "others",
                    candidateName = "Independents / Others",
                    partyName = "Regional Parties & Independents",
                    partyCode = "OTH",
                    winProbability = 0.12f,
                    webTractionIndex = 78,
                    newsSentimentPositive = 0.52f,
                    newsSentimentNeutral = 0.35f,
                    newsSentimentNegative = 0.13f,
                    publicPulseOptimism = 0.68f,
                    publicPulseKeyThought = "Strong preference for independent ward representatives who are free of party dictates and directly responsive to neighborhood needs.",
                    publicPulseConcern = "Lack of formal party structures limits their ability to negotiate large municipal budgets.",
                    dataGroundingSummary = "Derived from local resident welfare association surveys and independent ward results.",
                    partyColor = Color(0xFF78909C)
                )
            )
        )
    }

    // 4. Filter the data based on user options
    val currentProjectionsList = projectionsMap[selectedElectionType] ?: emptyList()
    
    val filteredProjectionsList = remember(selectedPartyFilter, currentProjectionsList) {
        if (selectedPartyFilter == "ALL") {
            currentProjectionsList
        } else {
            currentProjectionsList.filter { it.partyCode == selectedPartyFilter }
        }
    }

    // Safely update selected candidate if filtered out
    LaunchedEffect(filteredProjectionsList) {
        if (filteredProjectionsList.none { it.candidateId == selectedCandidateId }) {
            filteredProjectionsList.firstOrNull()?.let {
                selectedCandidateId = it.candidateId
            }
        }
    }

    val selectedProjection = currentProjectionsList.find { it.candidateId == selectedCandidateId } 
        ?: currentProjectionsList.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("electoral_intelligence_dashboard"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Election Type Filter Row
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "INDIAN ELECTION TYPE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val electionTypes = listOf(
                    "LOK_SABHA" to "Lok Sabha (General)",
                    "VIDHAN_SABHA" to "Vidhan Sabha (State)",
                    "MUNICIPAL" to "Local Bodies (Municipal)"
                )
                electionTypes.forEach { (type, label) ->
                    val isSelected = selectedElectionType == type
                    Button(
                        onClick = { 
                            selectedElectionType = type
                            // Reset candidate to ensure consistent lookup
                            selectedCandidateId = if (type == "MUNICIPAL") "arvind_kejriwal" else "narendra_modi"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("election_type_filter_$type"),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Party Filter Dropdown/Pills
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PARTY FILTER",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val parties = listOf(
                    "ALL" to "All",
                    "BJP" to "BJP",
                    "INC" to "INC",
                    "AAP" to "AAP",
                    "OTH" to "Others"
                )
                parties.forEach { (code, label) ->
                    val isSelected = selectedPartyFilter == code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.secondary 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedPartyFilter = code }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Horizontal Interactive Win Probability Chart Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Chart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "WIN PROBABILITY INDEX",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Based on web traction, news index & people thoughts",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Source grounding label
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live Synthesized",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Plotted Candidates Interactive Rows
                if (filteredProjectionsList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No candidates match the active filters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredProjectionsList.forEach { proj ->
                            val isSelected = selectedCandidateId == proj.candidateId
                            
                            val animatedWidth by animateFloatAsState(
                                targetValue = proj.winProbability,
                                animationSpec = tween(1000, easing = FastOutSlowInEasing),
                                label = "prob_width_${proj.candidateId}"
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedCandidateId = proj.candidateId }
                                    .padding(8.dp)
                            ) {
                                // Candidate and Party Labels
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Colored indicator square
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(proj.partyColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = proj.candidateName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${proj.partyCode})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Win percentage text
                                    Text(
                                        text = "${(proj.winProbability * 100).toInt()}% Chance",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = proj.partyColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // The bar representation (Compose Canvas)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val fillWidth = size.width * animatedWidth
                                        drawRoundRect(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    proj.partyColor.copy(alpha = 0.7f),
                                                    proj.partyColor
                                                )
                                            ),
                                            size = Size(fillWidth, size.height),
                                            cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Dynamic Micro Stats indicators
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = "Web Interest",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Web Interest: ${proj.webTractionIndex}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.RecordVoiceOver,
                                            contentDescription = "Public thoughts",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Public Sentiment: ${(proj.publicPulseOptimism * 100).toInt()}%",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Detailed Breakdown of Selected Candidate Analytics
        if (selectedProjection != null) {
            AnimatedContent(
                targetState = selectedProjection,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "breakdown_anim"
            ) { projection ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(projection.partyColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "Details",
                                    tint = projection.partyColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${projection.candidateName.uppercase()} ANALYSIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = projection.partyColor,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Multidimensional Data Grounding",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

                        // Web and News analysis section
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RssFeed,
                                    contentDescription = "Web & News",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Web & Press Grounding Evidence",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = projection.dataGroundingSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }

                        // Sentiment Index Split (Positive, Neutral, Negative)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "News Sentiment Breakdown",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(projection.newsSentimentPositive * 100).toInt()}% positive index",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00B050)
                                )
                            }

                            // Horizontal stacked progress bar for News Sentiment
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(projection.newsSentimentPositive)
                                        .background(Color(0xFF2E7D32)) // Green
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(projection.newsSentimentNeutral)
                                        .background(Color(0xFFFFC000)) // Amber
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(projection.newsSentimentNegative)
                                        .background(Color(0xFFC62828)) // Red
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LegendItem("Positive", Color(0xFF2E7D32))
                                LegendItem("Neutral", Color(0xFFFFC000))
                                LegendItem("Critical", Color(0xFFC62828))
                            }
                        }

                        // People's thoughts pulse / public pulse
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "Pulse",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "People's Thoughts & Public Pulse",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Support",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = projection.publicPulseKeyThought,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Concern",
                                        tint = Color(0xFFEF6C00),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = projection.publicPulseConcern,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
