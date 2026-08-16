package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.remote.CandidateCitation
import com.example.data.remote.CandidateFactCheck
import com.example.data.remote.CandidateQueryResponse
import com.example.ui.components.CivicLensErrorDisplay
import com.example.ui.components.ErrorType
import com.example.ui.components.GlassCard
import com.example.ui.components.OfflineBanner
import com.example.viewmodel.CivicLensViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateSearchScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val searchQuery by viewModel.candidateSearchQuery.collectAsState()
    val searchFocus by viewModel.candidateSearchFocus.collectAsState()
    val searchResult by viewModel.candidateSearchResult.collectAsState()
    val isLoading by viewModel.isCandidateSearchLoading.collectAsState()
    val errorMsg by viewModel.candidateSearchError.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val popularCandidates = remember {
        listOf(
            "Narendra Modi",
            "Rahul Gandhi",
            "Mamata Banerjee",
            "Arvind Kejriwal",
            "Nitin Gadkari",
            "Shashi Tharoor",
            "Akhilesh Yadav"
        )
    }

    val focusFilters = remember {
        listOf(
            "All" to "All Info",
            "Affidavits & Assets" to "Affidavits & Assets",
            "Track Record" to "Track Record",
            "Fact Checks" to "Fact Checks",
            "Key Policies" to "Key Policies"
        )
    }

    // Default search if query is empty on start
    LaunchedEffect(Unit) {
        if (searchResult == null && searchQuery.isBlank()) {
            viewModel.performCandidateSearch("Narendra Modi")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "CivicLens AI Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = "Candidate AI Search",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Gemini 3.5-Flash Grounded Intelligence",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToCompare,
                        modifier = Modifier.testTag("compare_shortcut_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = "Compare",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OfflineBanner(
                    isOnline = isOnline,
                    onRetry = { viewModel.retryConnection() }
                )
            }

            // 1. Search Bar Input
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("candidate_search_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateCandidateSearchQuery(it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("candidate_search_input"),
                                placeholder = {
                                    Text(
                                        "Search candidate (e.g. Narendra Modi, Rahul Gandhi)",
                                        fontSize = 13.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.clearCandidateSearch() }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { viewModel.performCandidateSearch() },
                                modifier = Modifier
                                    .height(56.dp)
                                    .testTag("candidate_search_button"),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !isLoading && searchQuery.isNotBlank()
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "Query Gemini",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Analyze", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Filter focus chips
                        Text(
                            text = "Filter Intelligence Focus:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.testTag("candidate_filter_chips_row")
                        ) {
                            items(focusFilters) { (key, label) ->
                                val selected = searchFocus == key
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.setCandidateSearchFocus(key) },
                                    label = { Text(label, fontSize = 12.sp) },
                                    leadingIcon = if (selected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    modifier = Modifier.testTag("candidate_filter_chip_$key")
                                )
                            }
                        }
                    }
                }
            }

            // 2. Popular Candidate Quick Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Popular Political Candidates:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("popular_candidates_row")
                    ) {
                        items(popularCandidates) { candidateName ->
                            AssistChip(
                                onClick = {
                                    viewModel.updateCandidateSearchQuery(candidateName)
                                    viewModel.performCandidateSearch(candidateName)
                                },
                                label = { Text(candidateName, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.testTag("popular_candidate_${candidateName.replace(" ", "_")}")
                            )
                        }
                    }
                }
            }

            // 3. Loading state
            if (isLoading) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .testTag("candidate_search_loading")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(42.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Querying Gemini 3.5-Flash Grounded AI...",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Cross-checking ECI sworn affidavits, asset declarations, parliamentary records, and PIB fact checks for non-partisan accuracy.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 4. Error State
            if (errorMsg != null && !isLoading) {
                item {
                    CivicLensErrorDisplay(
                        errorType = if (!isOnline) ErrorType.OFFLINE else ErrorType.API_FAILURE,
                        errorMessage = errorMsg,
                        onRetry = { viewModel.retryConnection() }
                    )
                }
            }

            // 5. Result View
            searchResult?.let { result ->
                val isBookmarked = bookmarks.any { it.id == "cand_query_${result.candidateName.hashCode()}" }

                item {
                    // Candidate Hero Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("candidate_hero_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = result.partyName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.candidateName,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${result.currentRole} • ${result.constituency}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Confidence badge
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.border(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        RoundedCornerShape(12.dp)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${(result.confidenceScore * 100).toInt()}%",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Grounded",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // Key Metrics Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Asset Badge
                                Column {
                                    Text("Declared Assets", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(result.declaredAssets, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }

                                // Criminal Cases Badge
                                Column {
                                    Text("Criminal Cases", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    val caseColor = if (result.criminalCasesCount == 0) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                    Text(
                                        text = if (result.criminalCasesCount == 0) "0 Cases" else "${result.criminalCasesCount} Pending",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = caseColor
                                    )
                                }

                                // Education Badge
                                Column {
                                    Text("Education", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = result.education,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                // Objective Summary Bio Card
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Objective Profile Summary",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = result.summaryBio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Key Stances & Policy Promises
                if (result.keyStancesAndPromises.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Policy,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Key Policy Stances & Manifestos",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                result.keyStancesAndPromises.forEach { stance ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stance,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Verified Achievements & Milestones
                if (result.verifiedAchievements.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFFFA000),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Verified Achievements & Milestones",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                result.verifiedAchievements.forEach { milestone ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFA000),
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = milestone,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Verified Fact-Checks & Claims
                if (result.controversiesAndFactChecks.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.AutoMirrored.Filled.FactCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fact-Checked Statements & Disclosures",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            result.controversiesAndFactChecks.forEach { factCheck ->
                                CandidateFactCheckCard(factCheck = factCheck)
                            }
                        }
                    }
                }

                // Official Grounding Citations
                if (result.officialCitations.isNotEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Official Grounding & ECI Records:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                result.officialCitations.forEach { citation ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (citation.url.startsWith("http")) {
                                                    try {
                                                        uriHandler.openUri(citation.url)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Opening link failed", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = citation.sourceName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Icon(
                                            Icons.AutoMirrored.Filled.Launch,
                                            contentDescription = "Open link",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Bar Footer
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val bookmarkId = "cand_query_${result.candidateName.hashCode()}"
                                viewModel.toggleBookmark(
                                    id = bookmarkId,
                                    title = "Candidate: ${result.candidateName} (${result.partyName})",
                                    type = "Candidate Query",
                                    itemId = result.candidateName,
                                    currentlyBookmarked = isBookmarked
                                )
                                Toast.makeText(
                                    context,
                                    if (isBookmarked) "Bookmark removed" else "Saved candidate analysis to Bookmarks!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bookmark_candidate_button"),
                            colors = if (isBookmarked) {
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            } else {
                                ButtonDefaults.buttonColors()
                            }
                        ) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBookmarked) "Saved" else "Save Result", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateToCompare,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("compare_candidate_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Compare", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.askAssistant("Tell me more about candidate ${result.candidateName} of ${result.partyName}")
                                onNavigateToAssistant()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ask_assistant_button")
                        ) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ask AI", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CandidateFactCheckCard(factCheck: CandidateFactCheck) {
    val (verdictColor, verdictBg) = when (factCheck.verdict.uppercase()) {
        "VERIFIED TRUE", "TRUE" -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
        "FALSE" -> Color(0xFFD32F2F) to Color(0xFFFFEBEE)
        "MISLEADING" -> Color(0xFFE65100) to Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fact_check_item_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = verdictBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = factCheck.verdict.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = verdictColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Icon(
                    Icons.Default.Shield,
                    contentDescription = "Verified Record",
                    tint = verdictColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "\"${factCheck.claimOrIssue}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = factCheck.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
