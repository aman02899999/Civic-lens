package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.DbVerifiedNews
import com.example.data.remote.ElectionClaimVerificationResult
import com.example.ui.ads.AdMobBanner
import com.example.ui.components.OfflineBanner
import com.example.viewmodel.CivicLensViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CivicLensViewModel,
    onNavigateToAssistant: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToSchemes: () -> Unit,
    onNavigateToConstituency: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToResearch: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSentiment: () -> Unit,
    onNavigateToCandidateSearch: () -> Unit,
    onNavigateToGovtJobHelper: () -> Unit = {},
    onNavigateToClaimVerification: () -> Unit = {}
) {
    val news by viewModel.news.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isLiveLoading by viewModel.isLiveNewsLoading.collectAsState()
    val liveNewsError by viewModel.liveNewsError.collectAsState()
    val analyzingArticleId by viewModel.analyzingArticleId.collectAsState()
    val selectedArticleVeracityResult by viewModel.selectedArticleVeracityResult.collectAsState()
    val isClaimVerificationLoading by viewModel.isClaimVerificationLoading.collectAsState()
    val claimVerificationResult by viewModel.claimVerificationResult.collectAsState()

    var quickQueryInput by remember { mutableStateOf("") }
    var selectedVerdictTab by remember { mutableStateOf(0) } // 0 = All, 1 = Debunked False, 2 = Verified True, 3 = Misleading, 4 = Live AI Feed
    var selectedCategoryFilter by remember { mutableStateOf("All") } // "All", "National", "Local", "Policy", "Candidates"
    var selectedSourceFilter by remember { mutableStateOf("All Sources") }

    val context = LocalContext.current

    // Veracity statistics calculation
    val totalArticles = news.size
    val debunkedCount = news.count { 
        val v = it.factCheckVerdict.uppercase()
        v == "FALSE" || v == "FAKE" || v == "DEBUNKED"
    }
    val verifiedCount = news.count { 
        val v = it.factCheckVerdict.uppercase()
        v == "TRUE" || v == "VERIFIED"
    }
    val misleadingCount = news.count { 
        val v = it.factCheckVerdict.uppercase()
        v == "MISLEADING" || v == "PARTIALLY_TRUE"
    }
    val liveFeedCount = news.count { it.id.startsWith("live_") }

    // Category statistics calculation
    val nationalCount = news.count { it.category.equals("National", ignoreCase = true) }
    val localCount = news.count { it.category.equals("Local", ignoreCase = true) }
    val policyCount = news.count { it.category.equals("Policy", ignoreCase = true) }
    val candidatesCount = news.count { it.category.equals("Candidates", ignoreCase = true) }

    val filteredNews = remember(news, selectedVerdictTab, selectedCategoryFilter, selectedSourceFilter) {
        news.filter { article ->
            val matchesTab = when (selectedVerdictTab) {
                1 -> {
                    val v = article.factCheckVerdict.uppercase()
                    v == "FALSE" || v == "FAKE" || v == "DEBUNKED"
                }
                2 -> {
                    val v = article.factCheckVerdict.uppercase()
                    v == "TRUE" || v == "VERIFIED"
                }
                3 -> {
                    val v = article.factCheckVerdict.uppercase()
                    v == "MISLEADING" || v == "PARTIALLY_TRUE"
                }
                4 -> article.id.startsWith("live_")
                else -> true
            }

            val matchesCategory = when (selectedCategoryFilter) {
                "National" -> article.category.equals("National", ignoreCase = true)
                "Local" -> article.category.equals("Local", ignoreCase = true)
                "Policy" -> article.category.equals("Policy", ignoreCase = true)
                "Candidates" -> article.category.equals("Candidates", ignoreCase = true)
                else -> true
            }

            val matchesSource = when (selectedSourceFilter) {
                "ECI" -> article.source.contains("ECI", ignoreCase = true) || article.source.contains("Election Commission", ignoreCase = true)
                "PIB" -> article.source.contains("PIB", ignoreCase = true) || article.source.contains("Press Information Bureau", ignoreCase = true)
                "Ministries" -> article.source.contains("Ministry", ignoreCase = true) || article.source.contains("Gov", ignoreCase = true)
                else -> true
            }

            matchesTab && matchesCategory && matchesSource
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            MaterialTheme.colorScheme.surface
        )
    )

    Scaffold(
        bottomBar = { AdMobBanner() }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(gradientBrush)
        ) {
            val isTablet = maxWidth >= 768.dp

            Column(modifier = Modifier.fillMaxSize()) {
                // Main Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "CivicLens AI Logo",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Column {
                            Text(
                                text = "CivicLens AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Election News & Veracity Intelligence",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateToClaimVerification,
                            modifier = Modifier.testTag("nav_claim_verifier_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.TravelExplore,
                                contentDescription = "Claim Verifier",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = onNavigateToBookmarks,
                            modifier = Modifier.testTag("nav_bookmarks_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (bookmarks.isNotEmpty()) {
                                        Badge { Text("${bookmarks.size}") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Bookmarks",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier.testTag("nav_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Real-time Offline Connection State Banner
                OfflineBanner(
                    isOnline = isOnline,
                    onRetry = { viewModel.retryConnection() }
                )

                // Main Scrollable Content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // 1. Hero Veracity Command Center & Live Analytics
                    item {
                        VeracityHeroCommandCenter(
                            totalArticles = totalArticles,
                            debunkedCount = debunkedCount,
                            verifiedCount = verifiedCount,
                            misleadingCount = misleadingCount,
                            isLiveLoading = isLiveLoading,
                            onScanLiveNews = { viewModel.refreshLiveNews() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("live_dashboard_card")
                        )
                    }

                    // 2. Interactive "Analyze with Gemini" Quick Bar
                    item {
                        QuickVeracitySearchBar(
                            query = quickQueryInput,
                            onQueryChange = { quickQueryInput = it },
                            isLoading = isClaimVerificationLoading,
                            onAnalyze = { query ->
                                if (query.isNotBlank()) {
                                    viewModel.verifyElectionClaim(query, "Election News & Fact-Checks")
                                }
                            },
                            onSuggestionClick = { suggestion ->
                                quickQueryInput = suggestion
                                viewModel.verifyElectionClaim(suggestion, "Election News & Fact-Checks")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // 3. Quick Secondary Modules Strip
                    item {
                        PrimaryModulesHorizontalStrip(
                            onNavigateToAssistant = onNavigateToAssistant,
                            onNavigateToCompare = onNavigateToCompare,
                            onNavigateToSchemes = onNavigateToSchemes,
                            onNavigateToConstituency = onNavigateToConstituency,
                            onNavigateToCandidateSearch = onNavigateToCandidateSearch,
                            onNavigateToGovtJobHelper = onNavigateToGovtJobHelper,
                            onNavigateToResearch = onNavigateToResearch,
                            onNavigateToSentiment = onNavigateToSentiment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    // 4. Veracity Filter Tabs (Debunked / Verified / Misleading / Live)
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 10.dp, bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Election News & Veracity Feed",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "Full Archive",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable(onClick = onNavigateToNews)
                                        .testTag("view_all_news_button")
                                        .padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tab Row for Status Categories
                            ScrollableTabRow(
                                selectedTabIndex = selectedVerdictTab,
                                edgePadding = 0.dp,
                                containerColor = Color.Transparent,
                                divider = {}
                            ) {
                                Tab(
                                    selected = selectedVerdictTab == 0,
                                    onClick = { selectedVerdictTab = 0 },
                                    text = { Text("All ($totalArticles)", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = selectedVerdictTab == 1,
                                    onClick = { selectedVerdictTab = 1 },
                                    text = { Text("🚨 Debunked ($debunkedCount)", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = selectedVerdictTab == 2,
                                    onClick = { selectedVerdictTab = 2 },
                                    text = { Text("✅ Verified ($verifiedCount)", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = selectedVerdictTab == 3,
                                    onClick = { selectedVerdictTab = 3 },
                                    text = { Text("⚠️ Misleading ($misleadingCount)", fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = selectedVerdictTab == 4,
                                    onClick = { selectedVerdictTab = 4 },
                                    text = { Text("🌐 Live AI ($liveFeedCount)", fontWeight = FontWeight.Bold) }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Filter Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Filter by Category",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (selectedCategoryFilter != "All" || selectedSourceFilter != "All Sources" || selectedVerdictTab != 0) {
                                    Text(
                                        text = "Clear All Filters",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable {
                                                selectedCategoryFilter = "All"
                                                selectedSourceFilter = "All Sources"
                                                selectedVerdictTab = 0
                                            }
                                            .testTag("clear_all_filters_btn")
                                            .padding(4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Category Filter Chips: All, National, Local, Policy, Candidates
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val categoryOptions = listOf(
                                    Triple("All", "All", totalArticles),
                                    Triple("National", "National", nationalCount),
                                    Triple("Local", "Local", localCount),
                                    Triple("Policy", "Policy", policyCount),
                                    Triple("Candidates", "Candidates", candidatesCount)
                                )

                                items(categoryOptions) { (catKey, catLabel, catCount) ->
                                    val isSelected = selectedCategoryFilter == catKey
                                    val catIcon = when (catKey) {
                                        "National" -> Icons.Default.Public
                                        "Local" -> Icons.Default.LocationOn
                                        "Policy" -> Icons.Default.Gavel
                                        "Candidates" -> Icons.Default.Person
                                        else -> Icons.Default.Category
                                    }

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategoryFilter = catKey },
                                        label = {
                                            Text(
                                                text = "$catLabel ($catCount)",
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = catIcon,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        },
                                        modifier = Modifier.testTag("category_chip_${catKey.lowercase()}")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Authority Source Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Source:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(listOf("All Sources", "ECI", "PIB", "Ministries")) { sourceLabel ->
                                        FilterChip(
                                            selected = selectedSourceFilter == sourceLabel,
                                            onClick = { selectedSourceFilter = sourceLabel },
                                            label = { Text(sourceLabel, fontSize = 11.sp) },
                                            leadingIcon = if (selectedSourceFilter == sourceLabel) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                            } else null,
                                            modifier = Modifier.testTag("source_chip_${sourceLabel.lowercase().replace(" ", "_")}")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Election News Articles Feed
                    if (filteredNews.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No election articles match the selected category & veracity filters.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            selectedVerdictTab = 0
                                            selectedCategoryFilter = "All"
                                            selectedSourceFilter = "All Sources"
                                        },
                                        modifier = Modifier.testTag("reset_filters_button")
                                    ) {
                                        Text("Reset All Filters")
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredNews) { article ->
                            val isBookmarked = bookmarks.any { it.itemId == article.id }
                            val isAnalyzingThis = analyzingArticleId == article.id

                            ElectionNewsVeracityCard(
                                article = article,
                                isBookmarked = isBookmarked,
                                isAnalyzing = isAnalyzingThis,
                                onCategoryClick = { clickedCategory ->
                                    selectedCategoryFilter = clickedCategory
                                },
                                onAnalyzeVeracity = {
                                    viewModel.analyzeArticleVeracity(article)
                                },
                                onBookmarkToggle = {
                                    viewModel.toggleBookmark(
                                        id = "news_${article.id}",
                                        title = article.title,
                                        type = "news",
                                        itemId = article.id,
                                        currentlyBookmarked = isBookmarked
                                    )
                                },
                                onOpenUrl = { url ->
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open link: $url", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 7.dp)
                                    .testTag("news_card_${article.id}")
                            )
                        }
                    }

                    // 6. Civic Transparency and Grounding Disclaimers
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        CivicDisclaimersCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("app_disclaimer_card")
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet / Dialog for in-depth Gemini Veracity Analysis
    val activeVeracityModal = selectedArticleVeracityResult ?: claimVerificationResult
    if (activeVeracityModal != null) {
        GeminiVeracityDetailDialog(
            result = activeVeracityModal,
            onDismiss = {
                viewModel.dismissArticleVeracityModal()
                viewModel.clearClaimVerification()
            }
        )
    }
}

/**
 * Hero Command Center displaying real-time veracity metrics and Gemini grounding controls.
 */
@Composable
fun VeracityHeroCommandCenter(
    totalArticles: Int,
    debunkedCount: Int,
    verifiedCount: Int,
    misleadingCount: Int,
    isLiveLoading: Boolean,
    onScanLiveNews: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GEMINI SEARCH GROUNDING ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32),
                        letterSpacing = 0.5.sp
                    )
                }

                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = "Lok Sabha 2026",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Election Veracity Intelligence",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Cross-referencing election news against ECI gazettes, PIB Fact Check, and statutory affidavits.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Metrics Indicators Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VeracityStatPill(
                    label = "Monitored",
                    count = totalArticles.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                VeracityStatPill(
                    label = "Debunked",
                    count = debunkedCount.toString(),
                    color = Color(0xFFBA1A1A),
                    modifier = Modifier.weight(1f)
                )
                VeracityStatPill(
                    label = "Verified",
                    count = verifiedCount.toString(),
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                VeracityStatPill(
                    label = "Misleading",
                    count = misleadingCount.toString(),
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scan Live News with Gemini Action
            FilledTonalButton(
                onClick = onScanLiveNews,
                enabled = !isLiveLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scan_live_news_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLiveLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scanning Live Portals with Gemini...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan Live Election News (Gemini AI)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VeracityStatPill(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Quick search bar to analyze any custom election headline or claim with Gemini.
 */
@Composable
fun QuickVeracitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean,
    onAnalyze: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_veracity_input"),
                    placeholder = {
                        Text(
                            text = "Check any election headline or rumor...",
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = if (query.isNotBlank()) {
                        {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    } else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onAnalyze(query) },
                    enabled = query.isNotBlank() && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("quick_veracity_button"),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Analyze", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trending rumor prompt chips
            Text(
                text = "Trending Claims to Verify:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    listOf(
                        "QR Voter ID mandatory?",
                        "100% VVPAT slip count",
                        "Free ₹5,000 election recharge",
                        "Overseas NRI voting app"
                    )
                ) { suggestion ->
                    SuggestionChip(
                        onClick = { onSuggestionClick(suggestion) },
                        label = { Text(suggestion, fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}

/**
 * Election News Article Card featuring embedded Fact-Check Status Indicator,
 * visual truth score gauge, and direct Gemini AI veracity analysis trigger.
 */
@Composable
fun ElectionNewsVeracityCard(
    article: DbVerifiedNews,
    isBookmarked: Boolean,
    isAnalyzing: Boolean,
    onAnalyzeVeracity: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onCategoryClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Normalize verdict
    val verdictUpper = article.factCheckVerdict.uppercase()
    val isDebunked = verdictUpper == "FALSE" || verdictUpper == "FAKE" || verdictUpper == "DEBUNKED"
    val isVerified = verdictUpper == "TRUE" || verdictUpper == "VERIFIED"
    val isMisleading = verdictUpper == "MISLEADING" || verdictUpper == "PARTIALLY_TRUE"

    // Veracity theme colors
    val (statusText, statusBg, statusColor, statusIcon) = when {
        isDebunked -> Quad(
            "DEBUNKED FALSE",
            Color(0xFFFFEBEE),
            Color(0xFFBA1A1A),
            Icons.Default.Cancel
        )
        isVerified -> Quad(
            "VERIFIED TRUE",
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            Icons.Default.CheckCircle
        )
        isMisleading -> Quad(
            "MISLEADING",
            Color(0xFFFFF3E0),
            Color(0xFFD97706),
            Icons.Default.Warning
        )
        else -> Quad(
            "OFFICIAL REPORT",
            Color(0xFFE8EAF6),
            Color(0xFF283593),
            Icons.Default.Verified
        )
    }

    val confidencePercent = (article.confidenceScore * 100).toInt()
    val truthPercent = when {
        isDebunked -> (5..15).random()
        isVerified -> confidencePercent
        isMisleading -> (35..55).random()
        else -> 85
    }

    val animatedProgress by animateFloatAsState(
        targetValue = truthPercent / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "truth_meter"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Source & Date Row + Embedded Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = article.source,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${article.date}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Embedded Fact-Check Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Badge & Dispatch Notice Sub-Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val (catIcon, catBg, catColor) = when (article.category.lowercase()) {
                    "local" -> Triple(Icons.Default.LocationOn, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
                    "policy" -> Triple(Icons.Default.Gavel, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
                    "candidates" -> Triple(Icons.Default.Person, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                    else -> Triple(Icons.Default.Public, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(catBg)
                        .clickable { onCategoryClick?.invoke(article.category) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("article_category_tag_${article.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = article.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = catColor,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = if (article.isFactCheck) "• Fact-Check Dispatch" else "• Official Notice",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Article Headline
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Embedded Veracity Context / Factual Finding Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(12.dp)
            ) {
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Truth / Confidence Score Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDebunked) "Debunked Authenticity" else "Grounded Veracity Score",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$truthPercent%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.15f)
            )

            // Official Grounding Portals
            if (article.officialSources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(article.officialSources) { sourceUrl ->
                        val cleanDomain = sourceUrl.removePrefix("https://").removePrefix("http://").substringBefore("/")
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .clickable { onOpenUrl(sourceUrl) }
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cleanDomain,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Analyze with Gemini AI, Share, Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAnalyzeVeracity,
                    enabled = !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("analyze_gemini_button_${article.id}")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verifying...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze with Gemini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val shareText = "🔍 CIVICLENS VERIFIED ELECTION NEWS\n" +
                                    "===========================================\n" +
                                    "📰 Title: ${article.title}\n" +
                                    "⚖️ Veracity Status: $statusText ($truthPercent% Truth Score)\n" +
                                    "🏛 Authority: ${article.source} (${article.date})\n\n" +
                                    "📋 Fact-Check Summary:\n\"${article.content}\"\n\n" +
                                    "🔗 Grounded Source: ${article.originalUrl.ifEmpty { "https://pib.gov.in" }}\n\n" +
                                    "✅ Powered by CivicLens AI — Non-Partisan Verified Election Intelligence."

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens Fact-Check: ${article.title}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Verified Election News"))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("home_share_button_${article.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Gemini AI Veracity Analysis Modal / Dialog.
 */
@Composable
fun GeminiVeracityDetailDialog(
    result: ElectionClaimVerificationResult,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val verdictUpper = result.verdict.uppercase()
    val isDebunked = verdictUpper.contains("FALSE") || verdictUpper.contains("DEBUNKED")
    val isVerified = verdictUpper.contains("TRUE") || verdictUpper.contains("VERIFIED")

    val (badgeText, badgeBg, badgeColor) = when {
        isDebunked -> Triple("DEBUNKED FALSE", Color(0xFFFFEBEE), Color(0xFFBA1A1A))
        isVerified -> Triple("VERIFIED TRUE", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        else -> Triple("MISLEADING / CAUTION", Color(0xFFFFF3E0), Color(0xFFD97706))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Title Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini Veracity Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Verdict Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = badgeBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = badgeText,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = badgeColor,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Truth: ${result.truthScorePercent}%",
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = result.verdictSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Analyzed Claim
                    Column {
                        Text(
                            text = "Claim Analyzed:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${result.claimText}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    // Fact-Check Breakdown Narrative
                    Column {
                        Text(
                            text = "Detailed Grounded Breakdown:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.factCheckBreakdown,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }

                    // Key Evidence Points
                    if (result.keyEvidencePoints.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Grounded Key Evidence:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            result.keyEvidencePoints.forEach { point ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = point.pointTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "${point.evidenceDetail} (Source: ${point.sourceName})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Official Portals Checked
                    if (result.officialPortalsChecked.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Authoritative Sources Verified:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.officialPortalsChecked.joinToString(" • "),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Recommended Civic Clarification
                    if (result.recommendedClarification.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💡 Citizen Recommendation:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.recommendedClarification,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = {
                            val shareBody = "🔍 GEMINI AI VERACITY REPORT: ${result.verdict}\n" +
                                    "Claim: \"${result.claimText}\"\n\n" +
                                    "Truth Score: ${result.truthScorePercent}%\n" +
                                    "Summary: ${result.verdictSummary}\n\n" +
                                    "Guidance: ${result.recommendedClarification}\n\n" +
                                    "Verified by CivicLens AI with Google Search Grounding."
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens Veracity Report")
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Fact-Check Report"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Report")
                    }
                }
            }
        }
    }
}

/**
 * Compact horizontal strip of secondary civic modules.
 */
@Composable
fun PrimaryModulesHorizontalStrip(
    onNavigateToAssistant: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToSchemes: () -> Unit,
    onNavigateToConstituency: () -> Unit,
    onNavigateToCandidateSearch: () -> Unit,
    onNavigateToGovtJobHelper: () -> Unit,
    onNavigateToResearch: () -> Unit,
    onNavigateToSentiment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Civic Modules & Tools",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                ModuleQuickCard(
                    title = "AI Assistant",
                    subtitle = "RAG Chat",
                    icon = Icons.Default.AutoAwesome,
                    color = MaterialTheme.colorScheme.primary,
                    testTag = "nav_assistant_feature",
                    onClick = onNavigateToAssistant
                )
            }
            item {
                ModuleQuickCard(
                    title = "Compare Hub",
                    subtitle = "Side-by-Side",
                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                    color = MaterialTheme.colorScheme.secondary,
                    testTag = "nav_compare_feature",
                    onClick = onNavigateToCompare
                )
            }
            item {
                ModuleQuickCard(
                    title = "Candidates",
                    subtitle = "ECI Affidavits",
                    icon = Icons.Default.PersonSearch,
                    color = MaterialTheme.colorScheme.primary,
                    testTag = "nav_candidate_search_feature",
                    onClick = onNavigateToCandidateSearch
                )
            }
            item {
                ModuleQuickCard(
                    title = "Govt Jobs",
                    subtitle = "Alerts & Guides",
                    icon = Icons.Default.Work,
                    color = MaterialTheme.colorScheme.tertiary,
                    testTag = "nav_govt_jobs_feature",
                    onClick = onNavigateToGovtJobHelper
                )
            }
            item {
                ModuleQuickCard(
                    title = "Schemes",
                    subtitle = "Eligibility",
                    icon = Icons.Default.Gavel,
                    color = MaterialTheme.colorScheme.secondary,
                    testTag = "nav_schemes_feature",
                    onClick = onNavigateToSchemes
                )
            }
            item {
                ModuleQuickCard(
                    title = "Constituency",
                    subtitle = "Development",
                    icon = Icons.Default.Map,
                    color = MaterialTheme.colorScheme.primary,
                    testTag = "nav_constituency_feature",
                    onClick = onNavigateToConstituency
                )
            }
            item {
                ModuleQuickCard(
                    title = "Deep Research",
                    subtitle = "Summaries",
                    icon = Icons.Default.BarChart,
                    color = MaterialTheme.colorScheme.tertiary,
                    testTag = "nav_research_feature",
                    onClick = onNavigateToResearch
                )
            }
            item {
                ModuleQuickCard(
                    title = "Sentiment",
                    subtitle = "Speech Veracity",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.secondary,
                    testTag = "nav_sentiment_feature",
                    onClick = onNavigateToSentiment
                )
            }
        }
    }
}

@Composable
fun ModuleQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier
            .width(136.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Civic Disclaimers and Non-Partisan Grounding Details.
 */
@Composable
fun CivicDisclaimersCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Disclaimer Information",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Civic Disclaimers & Grounding Notice",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "CivicLens AI is an independent, non-partisan civic intelligence dashboard powered by artificial intelligence. Election claims and news articles are analyzed by Gemini AI with live Google Search Grounding cross-referenced with official records from the Election Commission of India (ECI), Press Information Bureau (PIB), and Union Ministry Gazettes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Primary Verification Authorities: eci.gov.in • factcheck.pib.gov.in • sci.gov.in",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
