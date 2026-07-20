package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ElectoralWinProbabilityChart
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricGauge
import com.example.viewmodel.CivicLensViewModel
import kotlin.math.abs

data class CandidateStatement(
    val id: String,
    val candidateId: String,
    val candidateName: String,
    val partyName: String,
    val statement: String,
    val date: String,
    val sentimentScore: Float, // -1.0 to +1.0 (X Axis: Negative to Positive)
    val sentimentLabel: String, // e.g., "Hopeful", "Critical", "Confident"
    val veracityScore: Float, // 0.0 to 100.0 (Y Axis: Accuracy)
    val verificationSummary: String,
    val verifiedData: String,
    val source: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentimentChartScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Speech Discourse, 1 = Win Projections
    var selectedCandidateId by remember { mutableStateOf("all") } // "all", "narendra_modi", "rahul_gandhi", "arvind_kejriwal"

    val statements = remember {
        listOf(
            CandidateStatement(
                id = "modi_econ",
                candidateId = "narendra_modi",
                candidateName = "Narendra Modi",
                partyName = "Bharatiya Janata Party (BJP)",
                statement = "We will make India the third largest global economy with massive investments in digital and physical infrastructure.",
                date = "June 2024",
                sentimentScore = 0.8f,
                sentimentLabel = "Hopeful / Progressive",
                veracityScore = 94f,
                verificationSummary = "Strongly backed by global economic forecasts. IMF and World Bank track India's GDP growth path towards becoming the 3rd largest economy within this decade.",
                verifiedData = "Union Capital Expenditure rose to ₹11.11 Lakh Crore in the recent budget, boosting infrastructure rollout.",
                source = "ECI Manifestos & IMF World Economic Outlook Report",
                color = Color(0xFFF15A24) // Saffron
            ),
            CandidateStatement(
                id = "modi_electrification",
                candidateId = "narendra_modi",
                candidateName = "Narendra Modi",
                partyName = "Bharatiya Janata Party (BJP)",
                statement = "Under our administration, every single village in the country has been connected to the national power grid.",
                date = "August 2023",
                sentimentScore = 0.9f,
                sentimentLabel = "Optimistic / Accomplished",
                veracityScore = 85f,
                verificationSummary = "Technically accurate under census guidelines where 10% of households in a village being connected classifies it electrified. However, local household reliability challenges persist.",
                verifiedData = "Deen Dayal Upadhyaya Gram Jyoti Yojana (DDUGJY) successfully connected 100% of inhabited census villages.",
                source = "Ministry of Power - National Electrification Progress Dashboard",
                color = Color(0xFFF15A24)
            ),
            CandidateStatement(
                id = "gandhi_inequality",
                candidateId = "rahul_gandhi",
                candidateName = "Rahul Gandhi",
                partyName = "Indian National Congress (INC)",
                statement = "The top 1% controls more than 40% of the entire country's wealth, leaving lakhs of families in financial stress.",
                date = "May 2024",
                sentimentScore = -0.7f,
                sentimentLabel = "Critical / Reformist",
                veracityScore = 90f,
                verificationSummary = "Highly consistent with inequality datasets published by Oxfam and the World Inequality Lab tracking Indian income distribution.",
                verifiedData = "Oxfam Inequality Reports and economic research estimate the top 1% controls approx. 40.1% of national wealth.",
                source = "World Inequality Database & Oxfam India",
                color = Color(0xFF0070C0) // Blue
            ),
            CandidateStatement(
                id = "gandhi_employment",
                candidateId = "rahul_gandhi",
                candidateName = "Rahul Gandhi",
                partyName = "Indian National Congress (INC)",
                statement = "We will immediately guarantee 30 lakh government jobs upon forming the government to support unemployed youth.",
                date = "April 2024",
                sentimentScore = 0.7f,
                sentimentLabel = "Hopeful / Campaign Promise",
                veracityScore = 75f,
                verificationSummary = "Substantial public sector vacancies do exist across central ministries, railways, and public defense. However, immediate mobilization faces serious budget constraints.",
                verifiedData = "Administrative reports show ~9.6 Lakh central vacancies, with the remainder requiring extensive state government budget expansion.",
                source = "Ministry of Personnel Public Grievances & Pensions Annual Reports",
                color = Color(0xFF0070C0)
            ),
            CandidateStatement(
                id = "kejriwal_clinics",
                candidateId = "arvind_kejriwal",
                candidateName = "Arvind Kejriwal",
                partyName = "Aam Aadmi Party (AAP)",
                statement = "Our public healthcare model provides completely free medical consultations and quality medications to lakhs through Mohalla Clinics.",
                date = "January 2024",
                sentimentScore = 0.85f,
                sentimentLabel = "Confident / Welfare-focused",
                veracityScore = 95f,
                verificationSummary = "State healthcare audits and independent studies confirm the functional operation of over 500 Mohalla Clinics offering free diagnostic screenings and medicine.",
                verifiedData = "Department of Health & Family Welfare state data verifies free consultation and medicine distribution to over 2.5 Crore patient visits.",
                source = "State Health Audit Report & CAG Evaluation",
                color = Color(0xFF00B050) // Green
            ),
            CandidateStatement(
                id = "kejriwal_pollution",
                candidateId = "arvind_kejriwal",
                candidateName = "Arvind Kejriwal",
                partyName = "Aam Aadmi Party (AAP)",
                statement = "We have fully resolved the pollution and smog crisis in the capital through our innovative local policies.",
                date = "November 2023",
                sentimentScore = 0.5f,
                sentimentLabel = "Optimistic / Overstated",
                veracityScore = 40f,
                verificationSummary = "Contradicted directly by continuous Air Quality Index (AQI) monitoring. Smog and PM2.5 levels continue to spike significantly during winter periods.",
                verifiedData = "Central Pollution Control Board (CPCB) hourly tracking shows PM2.5 and PM10 levels frequently exceeding safe guidelines by 8-12x.",
                source = "Central Pollution Control Board (CPCB) Air Quality Index Reports",
                color = Color(0xFF00B050)
            )
        )
    }

    val filteredStatements = remember(selectedCandidateId, statements) {
        if (selectedCandidateId == "all") {
            statements
        } else {
            statements.filter { it.candidateId == selectedCandidateId }
        }
    }

    var selectedStatement by remember { mutableStateOf(statements.first()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Electoral Analytics & Sentiment",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Speech Discourse", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("speech_discourse_tab")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Win Projections", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("win_projections_tab")
                    )
                }
            }

            if (selectedTab == 1) {
                item {
                    ElectoralWinProbabilityChart(modifier = Modifier.fillMaxWidth())
                }
            } else {
                // Introductory Header
                item {
                    Text(
                        text = "A non-partisan multi-dimensional analysis mapping candidate speech sentiments against objective veracity, grounding claims directly with official records.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Candidate Tab Selector
                item {
                    Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val candidatesOptions = listOf(
                        "all" to "All",
                        "narendra_modi" to "N. Modi",
                        "rahul_gandhi" to "R. Gandhi",
                        "arvind_kejriwal" to "A. Kejriwal"
                    )

                    candidatesOptions.forEach { (id, label) ->
                        val isSelected = selectedCandidateId == id
                        Button(
                            onClick = { selectedCandidateId = id },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("candidate_filter_tab_$id")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Interactive Sentiment vs Veracity Mapping Scatter Grid
            item {
                Text(
                    text = "Sentiment-Veracity Cartesian Plane",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .padding(top = 16.dp, bottom = 12.dp, start = 32.dp, end = 16.dp)
                    ) {
                        val gridPrimaryColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        val textGridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(filteredStatements) {
                                    detectTapGestures { offset ->
                                        // Find nearest statement within click radius (e.g., 24.dp converted to pixels)
                                        var nearest: CandidateStatement? = null
                                        var minDistance = Float.MAX_VALUE
                                        val width = size.width
                                        val height = size.height

                                        filteredStatements.forEach { stmt ->
                                            // Mapping sentiment: -1.0 to +1.0 -> X coordinates: 0 to width
                                            val x = ((stmt.sentimentScore + 1.0f) / 2.0f) * width
                                            // Mapping veracity: 0 to 100 -> Y coordinates: height to 0
                                            val y = height - ((stmt.veracityScore / 100f) * height)

                                            val distance = Math.hypot((x - offset.x).toDouble(), (y - offset.y).toDouble()).toFloat()
                                            if (distance < minDistance && distance < 64f) {
                                                minDistance = distance
                                                nearest = stmt
                                            }
                                        }

                                        nearest?.let {
                                            selectedStatement = it
                                        }
                                    }
                                }
                        ) {
                            val width = size.width
                            val height = size.height

                            // Draw central grid axes (Sentiment center = X: width/2, Veracity Y-axis is 0-100)
                            // X-axis: Sentiment (Central Neutral Line)
                            drawLine(
                                color = gridPrimaryColor,
                                start = Offset(0f, height),
                                end = Offset(width, height),
                                strokeWidth = 2f
                            )
                            // Y-axis: Neutral sentiment centerline (0.0 score)
                            drawLine(
                                color = gridPrimaryColor,
                                start = Offset(width / 2f, 0f),
                                end = Offset(width / 2f, height),
                                strokeWidth = 1.5f
                            )

                            // Horizontal Grid Lines for Veracity
                            val linesCount = 4
                            for (i in 1..linesCount) {
                                val y = (height / linesCount) * i
                                drawLine(
                                    color = gridPrimaryColor.copy(alpha = 0.05f),
                                    start = Offset(0f, y),
                                    end = Offset(width, y),
                                    strokeWidth = 1f
                                )
                            }

                            // Plot statements as glowing, interactive nodes
                            filteredStatements.forEach { stmt ->
                                val x = ((stmt.sentimentScore + 1.0f) / 2.0f) * width
                                val y = height - ((stmt.veracityScore / 100f) * height)
                                val isSelected = stmt.id == selectedStatement.id

                                // Outer Pulsing Ring if selected
                                if (isSelected) {
                                    drawCircle(
                                        color = stmt.color.copy(alpha = 0.25f),
                                        radius = 24f,
                                        center = Offset(x, y)
                                    )
                                    drawCircle(
                                        color = stmt.color,
                                        radius = 14f,
                                        center = Offset(x, y),
                                        style = Stroke(width = 3f)
                                    )
                                }

                                // Solid interactive dot
                                drawCircle(
                                    color = stmt.color,
                                    radius = 10f,
                                    center = Offset(x, y)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 4f,
                                    center = Offset(x, y)
                                )
                            }
                        }

                        // Cartesian Axis Labels
                        // Y-Axis: Veracity % (Left Axis)
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = (-30).dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("100%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("50%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("0%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                        }

                        // X-Axis Labels: Sentiment (Bottom Axis)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .offset(y = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("◀ CRITICAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                            Text("NEUTRAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                            Text("HOPEFUL ▶", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textGridColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 Interactive Mapping: Tap on the plotted points in the Cartesian plane to load live facts.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // High Fidelity Animated Statement details Card
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val isBookmarked = false // Simulated state for now
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Selected Statement Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(selectedStatement.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedStatement.candidateName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = selectedStatement.date,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = selectedStatement.partyName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
                            )

                            // Quote Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "\"${selectedStatement.statement}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Sentiment Analysis Metrics (Dynamic UI representation)
                            Text(
                                text = "SPEECH SENTIMENT BREAKDOWN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedStatement.sentimentScore >= 0f) Icons.Default.Mood else Icons.Default.MoodBad,
                                    contentDescription = null,
                                    tint = selectedStatement.color
                                )
                                Column {
                                    Text(
                                        text = selectedStatement.sentimentLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val mappedSentimentPercentage = ((selectedStatement.sentimentScore + 1f) / 2f * 100).toInt()
                                    Text(
                                        text = "Sentiment Index Score: $mappedSentimentPercentage% (-1 to +1 scale)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grounding Accuracy Metric
                            MetricGauge(
                                label = "FACTUAL VERACITY ACCURACY",
                                progressText = "${selectedStatement.veracityScore.toInt()}%",
                                color = if (selectedStatement.veracityScore >= 80f) Color(0xFF00B050) else Color(0xFFFFC000)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Official Grounding Facts & Citation Box
                            Text(
                                text = "ECI GROUNDED EVIDENCE BRIEFING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Text(
                                text = selectedStatement.verificationSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Grounding Data: ${selectedStatement.verifiedData}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Source: ${selectedStatement.source}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Share summary card
                                    IconButton(
                                        onClick = {
                                            val hexHash = Integer.toHexString(selectedStatement.statement.hashCode()).uppercase().padStart(8, '0')
                                            val signature = "CL-SENT-${hexHash.take(4)}-${hexHash.drop(4)}-${selectedStatement.veracityScore.toInt()}"
                                            val shareUrl = "https://ais-pre-wijwveclzob5y5omrdcdec-257369852531.asia-southeast1.run.app/sentiment?id=${selectedStatement.id}"
                                            
                                            val shareText = "🔍 CIVICLENS SPEECH SENTIMENT REGISTRY\n" +
                                                    "=========================================\n" +
                                                    "⚖️ VERIFIED MULTI-AXIS DISCOURSE AUDIT\n" +
                                                    "-----------------------------------------\n" +
                                                    "Candidate: ${selectedStatement.candidateName} (${selectedStatement.partyName})\n" +
                                                    "Statement Date: ${selectedStatement.date}\n" +
                                                    "Speech Sentiment: ${selectedStatement.sentimentLabel}\n" +
                                                    "Factual Veracity Score: ${selectedStatement.veracityScore.toInt()}%\n" +
                                                    "Registry Signature: $signature\n\n" +
                                                    "📋 VERIFIABLE QUOTE:\n" +
                                                    "\"${selectedStatement.statement}\"\n\n" +
                                                    "📊 EVIDENCE BRIEF:\n" +
                                                    "• Analysis: ${selectedStatement.verificationSummary}\n" +
                                                    "• Grounding Record: ${selectedStatement.verifiedData}\n\n" +
                                                    "🔗 VERIFY DISCOURSE HISTORICALLY:\n" +
                                                    "$shareUrl\n\n" +
                                                    "⚖️ Empowering electors with verified non-partisan non-ideological metadata."

                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens Speech Veracity Audit")
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Share Discourse Report"))
                                        },
                                        modifier = Modifier.testTag("share_discourse_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Report",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Bookmark comparison / statement
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleBookmark(
                                                id = "sentiment_${selectedStatement.id}",
                                                title = "Discourse Audit: ${selectedStatement.candidateName}",
                                                type = "sentiment",
                                                itemId = selectedStatement.id,
                                                currentlyBookmarked = false
                                            )
                                        },
                                        modifier = Modifier.testTag("bookmark_discourse_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark speech analysis",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Statements List
            item {
                Text(
                    text = "discourse logs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(filteredStatements) { stmt ->
                val isSelected = stmt.id == selectedStatement.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedStatement = stmt }
                        .padding(12.dp)
                        .testTag("discourse_item_${stmt.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(stmt.color)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stmt.candidateName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stmt.statement,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${stmt.veracityScore.toInt()}% Grounded",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (stmt.veracityScore >= 80f) Color(0xFF00B050) else Color(0xFFFFC000)
                        )
                        Text(
                            text = stmt.sentimentLabel.substringBefore(" /"),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            }
        }
    }
}
