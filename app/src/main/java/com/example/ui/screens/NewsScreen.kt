package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbVerifiedNews
import com.example.ui.components.ClaimvsEvidenceCard
import com.example.ui.components.FactCheckBadge
import com.example.ui.components.FactCredibilityTagSet
import com.example.ui.components.GlassCard
import com.example.ui.components.VisualFactCheckingScorecard
import com.example.viewmodel.CivicLensViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val news by viewModel.news.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isLiveLoading by viewModel.isLiveNewsLoading.collectAsState()
    val liveError by viewModel.liveNewsError.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Fact Checks, 1 = Verified Press, 2 = Live AI Feed
    var selectedSourceFilter by remember { mutableStateOf("All Sources / Databases") }
    var selectedVerdictFilter by remember { mutableStateOf<String?>(null) }
    var filterExpanded by remember { mutableStateOf(false) }

    // "Live AI Grounded Feed" only ever matches live_-prefixed articles, which are exclusively
    // shown on the Live AI Feed tab — omit it elsewhere so it can't be picked as a dead-end filter.
    val filterOptions = if (selectedTab == 2) {
        listOf(
            "All Sources / Databases",
            "Press Information Bureau (PIB)",
            "Election Commission of India (ECI)",
            "Ministry Portals & PIB",
            "Live AI Grounded Feed"
        )
    } else {
        listOf(
            "All Sources / Databases",
            "Press Information Bureau (PIB)",
            "Election Commission of India (ECI)",
            "Ministry Portals & PIB"
        )
    }

    val baseFilteredNews = news.filter {
        when (selectedTab) {
            0 -> it.isFactCheck && !it.id.startsWith("live_")
            1 -> !it.isFactCheck && !it.id.startsWith("live_")
            else -> it.id.startsWith("live_")
        }
    }

    val verdictFilteredNews = if (selectedTab == 0 && selectedVerdictFilter != null) {
        baseFilteredNews.filter {
            val v = it.factCheckVerdict.uppercase()
            when (selectedVerdictFilter) {
                "TRUE" -> v == "TRUE" || v == "VERIFIED"
                "FALSE" -> v == "FALSE" || v == "FAKE" || v == "DEBUNKED"
                "MISLEADING" -> v == "MISLEADING"
                else -> true
            }
        }
    } else {
        baseFilteredNews
    }

    val filteredNews = verdictFilteredNews.filter { article ->
        when (selectedSourceFilter) {
            "Press Information Bureau (PIB)" -> {
                article.source.contains("PIB", ignoreCase = true) || 
                article.source.contains("Press Information Bureau", ignoreCase = true)
            }
            "Election Commission of India (ECI)" -> {
                article.source.contains("ECI", ignoreCase = true) || 
                article.source.contains("Election Commission", ignoreCase = true)
            }
            "Ministry Portals & PIB" -> {
                article.source.contains("Ministry", ignoreCase = true) ||
                article.source.contains("PIB", ignoreCase = true) ||
                article.source.contains("Gov", ignoreCase = true)
            }
            "Live AI Grounded Feed" -> {
                article.id.startsWith("live_")
            }
            else -> true
        }
    }

    LaunchedEffect(selectedTab) {
        selectedSourceFilter = "All Sources / Databases"
        selectedVerdictFilter = null
        if (selectedTab == 2 && baseFilteredNews.isEmpty() && !isLiveLoading) {
            viewModel.refreshLiveNews()
        }
    }

    var expandedArticleId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verified News & Fact-Checks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("news_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTab == 2) {
                        IconButton(
                            onClick = { viewModel.refreshLiveNews() },
                            modifier = Modifier.testTag("sync_live_news_button"),
                            enabled = !isLiveLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync Live News",
                                tint = if (isLiveLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                            )
                        }
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
                    onClick = {
                        selectedTab = 0
                        expandedArticleId = null
                    },
                    text = { Text("PIB Fact-Checks", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("fact_checks_tab")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        expandedArticleId = null
                    },
                    text = { Text("Verified Press", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("verified_news_tab")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        expandedArticleId = null
                    },
                    text = { Text("Live AI Feed", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("live_news_tab")
                )
            }

            // FILTER DROPDOWN BAR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 800.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("filter_selection_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Registry",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Outlet / Database Filter",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = when (selectedSourceFilter) {
                                    "All Sources / Databases" -> "Showing all verified records"
                                    else -> "Filtered: $selectedSourceFilter"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Box {
                        Button(
                            onClick = { filterExpanded = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("source_filter_dropdown_button")
                        ) {
                            Text(
                                text = when (selectedSourceFilter) {
                                    "All Sources / Databases" -> "All Databases"
                                    "Press Information Bureau (PIB)" -> "PIB India"
                                    "Election Commission of India (ECI)" -> "ECI Portal"
                                    "Ministry Portals & PIB" -> "Ministries & PIB"
                                    "Live AI Grounded Feed" -> "Live AI"
                                    else -> selectedSourceFilter
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Open Filter Menu",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .widthIn(min = 220.dp)
                        ) {
                            filterOptions.forEach { option ->
                                val isSelected = option == selectedSourceFilter
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedSourceFilter = option
                                        filterExpanded = false
                                        expandedArticleId = null
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when (option) {
                                                "All Sources / Databases" -> Icons.AutoMirrored.Filled.List
                                                "Press Information Bureau (PIB)" -> Icons.Default.Gavel
                                                "Election Commission of India (ECI)" -> Icons.Default.CheckCircle
                                                "Ministry Portals & PIB" -> Icons.Default.AccountBalance
                                                "Live AI Grounded Feed" -> Icons.Default.AutoAwesome
                                                else -> Icons.Default.Check
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier.testTag("filter_option_${option.replace(" ", "_")}")
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 2 && isLiveLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing Live Indian News with Gemini...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connecting to real-time Google search grounding to fetch, verify, and neutrally analyze Indian political context...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else if (selectedTab == 2 && liveError != null && filteredNews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Connection Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Failed to Fetch Live News",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = liveError ?: "An unknown error occurred.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.refreshLiveNews() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry Sync")
                        }
                    }
                }
            } else if (selectedTab != 0 && baseFilteredNews.isNotEmpty() && filteredNews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No Results",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Articles Match Filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No reports matching '$selectedSourceFilter' were found in this tab's registry database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { selectedSourceFilter = "All Sources / Databases" },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Clear Filter")
                        }
                    }
                }
            } else if (selectedTab != 0 && filteredNews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTab == 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RssFeed,
                                contentDescription = "Feed",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Live Grounded AI News Feed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pull the absolute latest Indian political news analyzed directly using Gemini and live search grounding.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(onClick = { viewModel.refreshLiveNews() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync Live Feed Now")
                            }
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isExpanded = maxWidth >= 600.dp
                val columns = if (isExpanded) GridCells.Adaptive(minSize = 340.dp) else GridCells.Fixed(1)

                LazyVerticalGrid(
                    columns = columns,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTab == 0) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            VisualFactCheckingScorecard(
                                factChecks = baseFilteredNews,
                                selectedVerdictFilter = selectedVerdictFilter,
                                onVerdictFilterSelected = { selectedVerdictFilter = it },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (filteredNews.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "No Results",
                                            modifier = Modifier.size(40.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No Factual Claims Match Filter",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Try tapping another segment on the scorecard above, or clear the active filters.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (selectedVerdictFilter != null) {
                                                Button(
                                                    onClick = { selectedVerdictFilter = null },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Clear Verdict Filter")
                                                }
                                            }
                                            if (selectedSourceFilter != "All Sources / Databases") {
                                                OutlinedButton(
                                                    onClick = { selectedSourceFilter = "All Sources / Databases" }
                                                ) {
                                                    Text("Clear Source Filter")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (selectedTab == 2) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Live Grounded Feed",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "This feed utilizes real-time Google search grounding to find, verify and analyze live Indian news neutrally.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    items(filteredNews) { article ->
                        val isExpanded = expandedArticleId == article.id
                        val isBookmarked = bookmarks.any { it.itemId == article.id && it.type == "news" }
                        NewsDetailCard(
                            article = article,
                            isExpanded = isExpanded,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(
                                    id = "news_${article.id}",
                                    title = article.title,
                                    type = "news",
                                    itemId = article.id,
                                    currentlyBookmarked = isBookmarked
                                )
                            },
                            onToggleExpand = {
                                expandedArticleId = if (isExpanded) null else article.id
                            },
                            onVerifyClaim = {
                                viewModel.askAssistant("Fact check this public statement: '${article.title}'. Cite official government records and state truthfulness score.")
                                onNavigateToAssistant()
                            },
                            modifier = Modifier.testTag("article_card_${article.id}")
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
fun NewsDetailCard(
    article: DbVerifiedNews,
    isExpanded: Boolean,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onToggleExpand: () -> Unit,
    onVerifyClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    GlassCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.source.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = article.date,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val confidencePct = (article.confidenceScore * 100).toInt()
                            val confidenceLabel = when {
                                article.confidenceScore >= 0.90 -> "HIGHLY TRUSTWORTHY"
                                article.confidenceScore >= 0.75 -> "MODERATELY VERIFIED"
                                else -> "UNVERIFIED"
                            }
                            val hexHash = Integer.toHexString(article.title.hashCode()).uppercase().padStart(8, '0')
                            val signature = "CL-NEWS-${hexHash.take(4)}-${hexHash.drop(4)}-$confidencePct"
                            val encodedTitle = Uri.encode(article.title)
                            val shareUrl = "https://ais-pre-wijwveclzob5y5omrdcdec-257369852531.asia-southeast1.run.app/news?id=${article.id}"
                            
                            val shareText = "🔍 CIVICLENS VERIFIED NEWS REGISTRY\n" +
                                    "===========================================\n" +
                                    "📰 OFFICIAL VERIFIED REPORT\n" +
                                    "-------------------------------------------\n" +
                                    "Title: ${article.title}\n" +
                                    "Source: ${article.source}\n" +
                                    "Date: ${article.date}\n" +
                                    "Confidence Level: $confidencePct% ($confidenceLabel)\n" +
                                    "Registry Signature: $signature\n\n" +
                                    "📋 SUMMARY:\n" +
                                    "\"${article.content}\"\n\n" +
                                    "🔗 VERIFY ORIGINAL REPORT IN THE APP:\n" +
                                    "$shareUrl\n\n" +
                                    "⚖️ Guarding democratic discourse with non-partisan, ECI-grounded verified evidence."

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens Verified News: ${article.title}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Verified Report"))
                        },
                        modifier = Modifier.testTag("share_button_${article.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share report",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.testTag("bookmark_button_${article.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark report",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            FactCredibilityTagSet(
                source = article.source,
                confidenceScore = article.confidenceScore,
                isFactCheck = article.isFactCheck,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (article.isFactCheck) {
                Spacer(modifier = Modifier.height(8.dp))
                FactCheckBadge(verdict = article.factCheckVerdict)
            }

            // Expanded content section
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    if (article.isFactCheck) {
                        ClaimvsEvidenceCard(
                            news = article,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    } else {
                        Text(
                            text = article.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (article.officialSources.isNotEmpty()) {
                        Text(
                            text = "Official Source Documents:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        article.officialSources.forEach { source ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (source.startsWith("http")) {
                                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(source))
                                            context.startActivity(i)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = source,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onVerifyClaim,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify with RAG AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (article.originalUrl.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(article.originalUrl))
                                    context.startActivity(i)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Original Page", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Expand Arrow indicator
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Show Less" else "Show More",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }
    }
}
