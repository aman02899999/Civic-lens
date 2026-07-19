package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbCandidate
import com.example.data.local.DbPoliticalParty
import com.example.ui.components.GlassCard
import com.example.ui.components.CandidatePlatformComparisonCard
import com.example.viewmodel.CivicLensViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val context = LocalContext.current
    val parties by viewModel.parties.collectAsState()
    val candidates by viewModel.candidates.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val preselectedCandidates by viewModel.preselectedCandidates.collectAsState()
    val preselectedParties by viewModel.preselectedParties.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Compare Candidates, 1 = Compare Parties

    // Candidate Comparison State
    var candidate1 by remember { mutableStateOf<DbCandidate?>(null) }
    var candidate2 by remember { mutableStateOf<DbCandidate?>(null) }
    var c1Expanded by remember { mutableStateOf(false) }
    var c2Expanded by remember { mutableStateOf(false) }

    // Party Comparison State
    var party1 by remember { mutableStateOf<DbPoliticalParty?>(null) }
    var party2 by remember { mutableStateOf<DbPoliticalParty?>(null) }
    var p1Expanded by remember { mutableStateOf(false) }
    var p2Expanded by remember { mutableStateOf(false) }

    // Statement Verify State
    var statementCandidate by remember { mutableStateOf<DbCandidate?>(null) }
    var scExpanded by remember { mutableStateOf(false) }
    var statementInput by remember { mutableStateOf("") }

    // Initialize defaults or handle preselection
    LaunchedEffect(candidates, preselectedCandidates) {
        if (candidates.isNotEmpty()) {
            if (preselectedCandidates != null) {
                selectedTab = 0
                val (c1Id, c2Id) = preselectedCandidates!!
                candidate1 = candidates.find { it.id == c1Id } ?: candidates.getOrNull(0)
                candidate2 = candidates.find { it.id == c2Id } ?: candidates.getOrNull(1)
                viewModel.clearPreselectedCandidates()
            } else if (candidate1 == null && candidate2 == null) {
                candidate1 = candidates.getOrNull(0)
                candidate2 = candidates.getOrNull(1)
            }
        }
    }
    LaunchedEffect(parties, preselectedParties) {
        if (parties.isNotEmpty()) {
            if (preselectedParties != null) {
                selectedTab = 1
                val (p1Id, p2Id) = preselectedParties!!
                party1 = parties.find { it.id == p1Id } ?: parties.getOrNull(0)
                party2 = parties.find { it.id == p2Id } ?: parties.getOrNull(1)
                viewModel.clearPreselectedParties()
            } else if (party1 == null && party2 == null) {
                party1 = parties.getOrNull(0)
                party2 = parties.getOrNull(1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Civic Comparison Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("compare_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            
            // Tab Header Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Candidates Compare", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("candidates_tab")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Parties Compare", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("parties_tab")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Statement Verify", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("statement_tab")
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedTab == 0) {
                    // --- CANDIDATE COMPARISON TAB ---
                    item {
                        Text(
                            "Select Candidates to Compare",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Selectors Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Candidate 1 selector
                            Box(modifier = Modifier.weight(1.0f)) {
                                OutlinedCard(
                                    onClick = { c1Expanded = !c1Expanded },
                                    modifier = Modifier.fillMaxWidth().testTag("candidate_1_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(candidate1?.name ?: "Select candidate 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(expanded = c1Expanded, onDismissRequest = { c1Expanded = false }) {
                                    candidates.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.name) },
                                            onClick = {
                                                candidate1 = c
                                                c1Expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Candidate 2 selector
                            Box(modifier = Modifier.weight(1.0f)) {
                                OutlinedCard(
                                    onClick = { c2Expanded = !c2Expanded },
                                    modifier = Modifier.fillMaxWidth().testTag("candidate_2_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(candidate2?.name ?: "Select candidate 2", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(expanded = c2Expanded, onDismissRequest = { c2Expanded = false }) {
                                    candidates.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.name) },
                                            onClick = {
                                                candidate2 = c
                                                c2Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (candidate1 != null && candidate2 != null) {
                        val compareId = "compare_candidates_${candidate1!!.id}_${candidate2!!.id}"
                        val isBookmarked = bookmarks.any { it.id == compareId }
                        // side-by-side details
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Affidavit & Performance Mapping",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val shareText = "🔍 CIVICLENS COMPARISON REGISTRY\n" +
                                                        "===================================\n" +
                                                        "👥 CANDIDATE COMPARISON REPORT\n" +
                                                        "-----------------------------------\n" +
                                                        "Candidate 1: ${candidate1!!.name} (${candidate1!!.partyName})\n" +
                                                        "Candidate 2: ${candidate2!!.name} (${candidate2!!.partyName})\n" +
                                                        "Constituency: ${candidate1!!.constituencyName}\n" +
                                                        "Registry ID: $compareId\n" +
                                                        "ECI Affidavit Grounding Signature: COMP-CAN-${candidate1!!.id.take(4).uppercase()}-${candidate2!!.id.take(4).uppercase()}\n\n" +
                                                        "📊 COMPARISON SUMMARY:\n" +
                                                        "• Education: ${candidate1!!.education} vs ${candidate2!!.education}\n" +
                                                        "• Profession: ${candidate1!!.profession} vs ${candidate2!!.profession}\n" +
                                                        "• Declared Assets: ${candidate1!!.assets} vs ${candidate2!!.assets}\n" +
                                                        "• Criminal Cases: ${candidate1!!.declaredCriminalCases} vs ${candidate2!!.declaredCriminalCases}\n" +
                                                        "• MP Attendance: ${candidate1!!.attendance} vs ${candidate2!!.attendance}\n\n" +
                                                        "🔗 VIEW LIVE DEEP COMPARISON:\n" +
                                                        "https://ais-pre-wijwveclzob5y5omrdcdec-257369852531.asia-southeast1.run.app/compare?type=candidate&id1=${candidate1!!.id}&id2=${candidate2!!.id}\n\n" +
                                                        "⚖️ Non-partisan, ECI-grounded verified metrics. Empowering civic awareness."
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_SUBJECT, "CivicLens Candidate Comparison")
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Share Candidate Comparison"))
                                            },
                                            modifier = Modifier.testTag("share_compare_candidates_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share comparison",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.toggleBookmark(
                                                    id = compareId,
                                                    title = "Candidate Compare: ${candidate1!!.name} vs ${candidate2!!.name}",
                                                    type = "compare_candidates",
                                                    itemId = "${candidate1!!.id}_${candidate2!!.id}",
                                                    currentlyBookmarked = isBookmarked
                                                )
                                            },
                                            modifier = Modifier.testTag("bookmark_compare_candidates_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark comparison",
                                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                CompareAttributeRow("Party", candidate1!!.partyName, candidate2!!.partyName)
                                CompareAttributeRow("Constituency", candidate1!!.constituencyName, candidate2!!.constituencyName)
                                CompareAttributeRow("Education", candidate1!!.education, candidate2!!.education)
                                CompareAttributeRow("Profession", candidate1!!.profession, candidate2!!.profession)
                                CompareAttributeRow("Declared Assets", candidate1!!.assets, candidate2!!.assets)
                                CompareAttributeRow("Liabilities", candidate1!!.liabilities, candidate2!!.liabilities)
                                CompareAttributeRow("Criminal Cases", "${candidate1!!.declaredCriminalCases}", "${candidate2!!.declaredCriminalCases}", isWarning = true)
                                CompareAttributeRow("Attendance", candidate1!!.attendance, candidate2!!.attendance)
                                CompareAttributeRow("Bills Intro", "${candidate1!!.billsIntroduced}", "${candidate2!!.billsIntroduced}")
                                CompareAttributeRow("History", candidate1!!.electionHistory, candidate2!!.electionHistory)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Platform & Key Policy Stances",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        item {
                            CandidatePlatformComparisonCard(
                                candidate1 = candidate1!!,
                                candidate2 = candidate2!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Ground with AI Summary Button
                        item {
                            Button(
                                onClick = {
                                    viewModel.askAssistant("Synthesize comparison analysis between Candidate ${candidate1!!.name} and Candidate ${candidate2!!.name} based on verified ECI affidavits.")
                                    onNavigateToAssistant()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ground_candidates_ai_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synthesize Comparison with AI", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (selectedTab == 1) {
                    // --- PARTY COMPARISON TAB ---
                    item {
                        Text(
                            "Select Political Parties to Compare",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Selectors Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Party 1 Selector
                            Box(modifier = Modifier.weight(1.0f)) {
                                OutlinedCard(
                                    onClick = { p1Expanded = !p1Expanded },
                                    modifier = Modifier.fillMaxWidth().testTag("party_1_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(party1?.name?.substringBefore("(")?.trim() ?: "Select Party 1", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(expanded = p1Expanded, onDismissRequest = { p1Expanded = false }) {
                                    parties.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.name) },
                                            onClick = {
                                                party1 = p
                                                p1Expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Party 2 Selector
                            Box(modifier = Modifier.weight(1.0f)) {
                                OutlinedCard(
                                    onClick = { p2Expanded = !p2Expanded },
                                    modifier = Modifier.fillMaxWidth().testTag("party_2_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(party2?.name?.substringBefore("(")?.trim() ?: "Select Party 2", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                                DropdownMenu(expanded = p2Expanded, onDismissRequest = { p2Expanded = false }) {
                                    parties.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.name) },
                                            onClick = {
                                                party2 = p
                                                p2Expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (party1 != null && party2 != null) {
                        val compareId = "compare_parties_${party1!!.id}_${party2!!.id}"
                        val isBookmarked = bookmarks.any { it.id == compareId }
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Manifesto & History Mapping",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                val shareText = "🔍 CIVICLENS COMPARISON REGISTRY\n" +
                                                        "===================================\n" +
                                                        "🏢 POLITICAL PARTY COMPARISON REPORT\n" +
                                                        "-----------------------------------\n" +
                                                        "Party 1: ${party1!!.name}\n" +
                                                        "Party 2: ${party2!!.name}\n" +
                                                        "Registry ID: $compareId\n" +
                                                        "ECI Manifesto Grounding Signature: COMP-PRTY-${party1!!.id.take(4).uppercase()}-${party2!!.id.take(4).uppercase()}\n\n" +
                                                        "📊 COMPARISON SUMMARY:\n" +
                                                        "• President: ${party1!!.president} vs ${party2!!.president}\n" +
                                                        "• Founded: ${party1!!.founded} vs ${party2!!.founded}\n" +
                                                        "• Seats History: ${party1!!.seatsHistory} vs ${party2!!.seatsHistory}\n" +
                                                        "• Vote Share: ${party1!!.voteShareHistory} vs ${party2!!.voteShareHistory}\n\n" +
                                                        "🔗 VIEW LIVE DEEP COMPARISON:\n" +
                                                        "https://ais-pre-wijwveclzob5y5omrdcdec-257369852531.asia-southeast1.run.app/compare?type=party&id1=${party1!!.id}&id2=${party2!!.id}\n\n" +
                                                        "⚖️ Non-partisan, ECI-grounded verified metrics. Empowering civic awareness."
                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_SUBJECT, "CivicLens Party Comparison")
                                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Share Party Comparison"))
                                            },
                                            modifier = Modifier.testTag("share_compare_parties_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share comparison",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                viewModel.toggleBookmark(
                                                    id = compareId,
                                                    title = "Party Compare: ${party1!!.name.substringBefore("(").trim()} vs ${party2!!.name.substringBefore("(").trim()}",
                                                    type = "compare_parties",
                                                    itemId = "${party1!!.id}_${party2!!.id}",
                                                    currentlyBookmarked = isBookmarked
                                                )
                                            },
                                            modifier = Modifier.testTag("bookmark_compare_parties_button")
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark comparison",
                                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                CompareAttributeRow("President", party1!!.president, party2!!.president)
                                CompareAttributeRow("Founded", party1!!.founded, party2!!.founded)
                                CompareAttributeRow("Manifesto Vision", party1!!.manifestoSummary, party2!!.manifestoSummary)
                                CompareAttributeRow("Seats History", party1!!.seatsHistory, party2!!.seatsHistory)
                                CompareAttributeRow("Vote Share", party1!!.voteShareHistory, party2!!.voteShareHistory)
                                CompareAttributeRow("Official Link", party1!!.officialWebsite, party2!!.officialWebsite)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Party Work Comparison: Achievements & Delivery Record",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Verified, publicly reported work delivered by each party, compared side-by-side.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth().testTag("party_work_comparison_card")) {
                                CompareAttributeRow(
                                    "Achievements & Work Done",
                                    party1!!.achievements.joinToString("\n") { "• $it" },
                                    party2!!.achievements.joinToString("\n") { "• $it" }
                                )
                                CompareAttributeRow(
                                    "Recent Press Releases",
                                    party1!!.pressReleases.joinToString("\n") { "• $it" },
                                    party2!!.pressReleases.joinToString("\n") { "• $it" }
                                )
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    viewModel.askAssistant("Compare the actual delivered work, achievements, and manifesto promises kept vs pending between ${party1!!.name} and ${party2!!.name}, citing official sources.")
                                    onNavigateToAssistant()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ground_parties_ai_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synthesize Work & Manifesto Comparison", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // --- STATEMENT VERIFICATION TAB ---
                    item {
                        Text(
                            "Candidate Statement Verifier",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    item {
                        val verificationResult by viewModel.statementVerificationState.collectAsState()
                        val isVerifying by viewModel.isVerificationLoading.collectAsState()
                        val verificationError by viewModel.verificationError.collectAsState()
                        var showStatementDetailsDialog by remember { mutableStateOf(false) }

                        // Initialize default statement candidate
                        LaunchedEffect(candidates) {
                            if (candidates.isNotEmpty() && statementCandidate == null) {
                                statementCandidate = candidates.getOrNull(0)
                            }
                        }

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Select Candidate Dropdown
                                Column {
                                    Text(
                                        "SELECT CANDIDATE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box {
                                        OutlinedCard(
                                            onClick = { scExpanded = !scExpanded },
                                            modifier = Modifier.fillMaxWidth().testTag("statement_candidate_selector")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    statementCandidate?.name ?: "Select Candidate",
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                            }
                                        }
                                        DropdownMenu(expanded = scExpanded, onDismissRequest = { scExpanded = false }) {
                                            candidates.forEach { c ->
                                                DropdownMenuItem(
                                                    text = { Text("${c.name} (${c.partyName})") },
                                                    onClick = {
                                                        statementCandidate = c
                                                        scExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Statement Input
                                Column {
                                    Text(
                                        "ENTER STATEMENT / CLAIM",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = statementInput,
                                        onValueChange = { statementInput = it },
                                        placeholder = { Text("e.g. GDP has doubled under our term or other policy claims...", fontSize = 13.sp) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(110.dp)
                                            .testTag("statement_text_input"),
                                        shape = RoundedCornerShape(12.dp),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                                    )
                                }

                                // Quick Suggestions Row
                                Column {
                                    Text(
                                        "POPULAR CLAIMS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val suggestions = listOf(
                                        "We successfully delivered zero-balance Jan Dhan bank accounts to over 50 crore citizens.",
                                        "Under our tenure, infrastructure spending reached an all-time high of 11 lakh crores.",
                                        "The current unemployment rate is at an all-time high of 15% across major urban areas.",
                                        "We have built over 11 crore household toilets across rural and semi-urban India."
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        suggestions.forEach { suggestion ->
                                            SuggestionChip(
                                                onClick = { statementInput = suggestion },
                                                label = { Text(suggestion.take(35) + "...", fontSize = 11.sp) },
                                                modifier = Modifier.testTag("suggestion_chip_${suggestion.take(10)}")
                                            )
                                        }
                                    }
                                }

                                // Verify Action Button
                                Button(
                                    onClick = {
                                        statementCandidate?.let {
                                            viewModel.verifyStatement(it.name, it.partyName, statementInput)
                                        }
                                    },
                                    enabled = statementInput.isNotBlank() && !isVerifying,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("verify_statement_button")
                                ) {
                                    if (isVerifying) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("AI Fact-Checking...")
                                    } else {
                                        Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Compare Side-by-Side with Facts", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Results rendering
                        if (isVerifying) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Text(
                                        "AI Fact-Checker Grounding in Progress...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Scanning official Election Commission databases, PIB fact-checks, and verified news repositories...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else if (verificationResult != null) {
                            val res = verificationResult!!
                            Text(
                                text = "Side-by-Side Fact-Check Comparison",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Left Side: Candidate Statement
                                Card(
                                    modifier = Modifier.weight(1f).testTag("candidate_statement_side"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.error)
                                            )
                                            Text(
                                                text = "CANDIDATE CLAIM",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        
                                        Text(
                                            text = "\"${res.statement}\"",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "By ${res.candidateName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = res.partyName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                // Right Side: Verified Fact-Check
                                Card(
                                    modifier = Modifier.weight(1f).testTag("verified_fact_side"),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val (verdictColor, verdictBg) = when (res.verdict.uppercase()) {
                                            "TRUE" -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
                                            "FALSE" -> Pair(Color(0xFFC62828), Color(0xFFFFEBEE))
                                            "MISLEADING" -> Pair(Color(0xFFEF6C00), Color(0xFFFFF3E0))
                                            "PARTIALLY_TRUE" -> Pair(Color(0xFFFBC02D), Color(0xFFFFFDE7))
                                            else -> Pair(Color(0xFF37474F), Color(0xFFECEFF1))
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(verdictColor)
                                            )
                                            Text(
                                                text = "VERIFIED FACT",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = verdictColor
                                            )
                                        }

                                        // Verdict Badge
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(verdictBg)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = res.verdict,
                                                color = verdictColor,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Text(
                                            text = res.explanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Source: ${res.factCheckSource}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grounding details
                            if (res.groundingPoints.isNotEmpty()) {
                                GlassCard(modifier = Modifier.fillMaxWidth().testTag("verification_details")) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Grounding Credibility Breakdown",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        res.groundingPoints.forEachIndexed { idx, point ->
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Text(
                                                    text = "•",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = point,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showStatementDetailsDialog = true },
                                            modifier = Modifier.fillMaxWidth().testTag("view_full_statement_report_button")
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("View Full Grounding Report", fontWeight = FontWeight.Bold)
                                        }

                                        if (res.sourceUrl.isNotBlank() && res.sourceUrl.startsWith("http")) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.sourceUrl))
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().testTag("visit_source_button"),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                            ) {
                                                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Visit Official Fact-Check Source", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            if (showStatementDetailsDialog) {
                                val (vColor, vBg) = when (res.verdict.uppercase()) {
                                    "TRUE" -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
                                    "FALSE" -> Pair(Color(0xFFC62828), Color(0xFFFFEBEE))
                                    "MISLEADING" -> Pair(Color(0xFFEF6C00), Color(0xFFFFF3E0))
                                    "PARTIALLY_TRUE" -> Pair(Color(0xFFFBC02D), Color(0xFFFFFDE7))
                                    else -> Pair(Color(0xFF37474F), Color(0xFFECEFF1))
                                }

                                AlertDialog(
                                    onDismissRequest = { showStatementDetailsDialog = false },
                                    title = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.VerifiedUser,
                                                    contentDescription = "Evidence Grounding",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Factual Integrity Registry",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            IconButton(
                                                onClick = { showStatementDetailsDialog = false },
                                                modifier = Modifier.testTag("close_statement_dialog")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close"
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
                                            // Verdict section
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(vBg)
                                                    .border(1.dp, vColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "FACT-CHECK VERDICT",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = vColor,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = res.verdict,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = vColor
                                                    )
                                                }
                                                
                                                val confidenceScorePct = (res.confidenceScore * 100).toInt()
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = "CONFIDENCE",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = vColor,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "$confidenceScorePct%",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = vColor
                                                    )
                                                }
                                            }

                                            // Candidate block
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                                    .padding(12.dp)
                                            ) {
                                                Text(
                                                    text = "CANDIDATE SOURCE",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${res.candidateName} (${res.partyName})",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            // Original Claim
                                            Column {
                                                Text(
                                                    text = "ANALYZED CANDIDATE CLAIM",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "\"${res.statement}\"",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            // Explanation
                                            Column {
                                                Text(
                                                    text = "VERIFICATION EXPLANATION",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = res.explanation,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    lineHeight = 20.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Grounding Points List
                                            if (res.groundingPoints.isNotEmpty()) {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    Text(
                                                        text = "KEY EVIDENCE & FACTS",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    res.groundingPoints.forEachIndexed { index, point ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(8.dp))
                                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                                                .padding(10.dp),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            verticalAlignment = Alignment.Top
                                                        ) {
                                                            Text(
                                                                text = "${index + 1}.",
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                fontSize = 12.sp
                                                            )
                                                            Text(
                                                                text = point,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurface,
                                                                lineHeight = 16.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Official References links
                                            if (res.sourceUrl.isNotBlank() && res.sourceUrl.startsWith("http")) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(
                                                        text = "VERIFIED REGISTRY LINK",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "Source: ${res.factCheckSource}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = res.sourceUrl,
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
                                                        val clipboard = LocalClipboardManager.current
                                                        val ctx = LocalContext.current
                                                        TextButton(
                                                            onClick = {
                                                                clipboard.setText(AnnotatedString(res.sourceUrl))
                                                                Toast.makeText(ctx, "Source link copied!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                            modifier = Modifier.height(32.dp).testTag("dialog_copy_stmt_link")
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
                                                        Button(
                                                            onClick = {
                                                                try {
                                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.sourceUrl))
                                                                    ctx.startActivity(intent)
                                                                } catch (e: Exception) {
                                                                    Toast.makeText(ctx, "Cannot open link", Toast.LENGTH_SHORT).show()
                                                                }
                                                            },
                                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                            modifier = Modifier.height(32.dp).testTag("dialog_visit_stmt_source")
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
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { showStatementDetailsDialog = false },
                                            modifier = Modifier.testTag("dismiss_statement_dialog")
                                        ) {
                                            Text("Done")
                                        }
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.testTag("statement_modal_dialog")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompareAttributeRow(
    attributeName: String,
    val1: String,
    val2: String,
    isWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = attributeName.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val warningColor = Color(0xFFC62828)
            val v1Cases = val1.toIntOrNull() ?: 0
            val v2Cases = val2.toIntOrNull() ?: 0

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = val1,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isWarning && v1Cases > 0) warningColor else MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = val2,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isWarning && v2Cases > 0) warningColor else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }
}
