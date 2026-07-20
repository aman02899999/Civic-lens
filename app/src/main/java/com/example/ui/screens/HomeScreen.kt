package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.DbVerifiedNews
import com.example.ui.components.FactCredibilityTagSet
import com.example.ui.components.GlassCard
import com.example.ui.components.PoliticalSearchInput
import com.example.ui.components.SeatsChart
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
    onNavigateToLegal: () -> Unit
) {
    val news by viewModel.news.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    var searchInput by remember { mutableStateOf("") }
    var activeSearch by remember { mutableStateOf(false) }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.background
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        val useDualPane = maxWidth >= 768.dp

        Column(modifier = Modifier.fillMaxSize()) {
            
            // App Title/Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "CivicLens AI",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            com.example.ui.theme.PremiumAccentGold,
                                            com.example.ui.theme.PremiumAccentGold.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("premium_badge")
                        ) {
                            Text(
                                text = "PREMIUM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                color = Color(0xFF3D2E00)
                            )
                        }
                    }
                    Text(
                        text = "Verified Election Intelligence",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateToBookmarks,
                        modifier = Modifier.testTag("nav_bookmarks_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarks",
                            tint = MaterialTheme.colorScheme.primary
                        )
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

            if (useDualPane) {
                // PREMIUM RESPONSIVE DUAL-PANE DESKTOP/TABLET GRID
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column (Search & Analytics Core)
                    Column(
                        modifier = Modifier
                            .weight(4.5f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PoliticalSearchInput(
                            onInvestigate = { query ->
                                viewModel.updateSearchQuery(query)
                                viewModel.askAssistant(query)
                                onNavigateToAssistant()
                            },
                            placeholderText = "Type a claim or political topic (e.g., 'EVM tampering')..."
                        )

                        if (searchHistory.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Recent Fact-Checks & Comparisons",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Clear All",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.clearSearchHistory() }
                                            .testTag("clear_search_history_button")
                                            .padding(4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(searchHistory.take(10)) { item ->
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.updateSearchQuery(item.query)
                                                    viewModel.askAssistant(item.query)
                                                    onNavigateToAssistant()
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .testTag("recent_query_${item.id}"),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.query,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("live_dashboard_card")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("LIVE TRACKER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    "Lok Sabha 2026",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SeatsChart(
                                title = "General Seats Distribution Projection",
                                parties = listOf(
                                    "NDA / Alliance" to 292,
                                    "INDIA Alliance" to 234,
                                    "Regional/Others" to 17
                                ),
                                totalSeats = 543
                            )
                        }
                    }

                    // Right Column (Features, Sentiment & Latest PIB news lists)
                    Column(
                        modifier = Modifier
                            .weight(5.5f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Primary Modules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "AI Assistant",
                                    subtitle = "RAG Verification Chat",
                                    icon = Icons.Default.AutoAwesome,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f).testTag("nav_assistant_feature"),
                                    onClick = onNavigateToAssistant
                                )
                                FeatureButton(
                                    title = "Compare Hub",
                                    subtitle = "Compare Candidates",
                                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f).testTag("nav_compare_feature"),
                                    onClick = onNavigateToCompare
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "Scheme Finder",
                                    subtitle = "Factual Eligibility",
                                    icon = Icons.Default.Gavel,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f).testTag("nav_schemes_feature"),
                                    onClick = onNavigateToSchemes
                                )
                                FeatureButton(
                                    title = "Constituency",
                                    subtitle = "Progress Dashboard",
                                    icon = Icons.Default.Map,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f).testTag("nav_constituency_feature"),
                                    onClick = onNavigateToConstituency
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "PIB Fact Checks",
                                    subtitle = "Verify Claims",
                                    icon = Icons.AutoMirrored.Filled.FactCheck,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f).testTag("nav_news_feature"),
                                    onClick = onNavigateToNews
                                )
                                FeatureButton(
                                    title = "Deep Research",
                                    subtitle = "Intensive Summaries",
                                    icon = Icons.Default.BarChart,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f).testTag("nav_research_feature"),
                                    onClick = onNavigateToResearch
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "Speech Sentiment Mapping",
                                    subtitle = "Compare Candidate Veracity",
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f).testTag("nav_sentiment_feature"),
                                    onClick = onNavigateToSentiment
                                )
                                FeatureButton(
                                    title = "Know Your Rights",
                                    subtitle = "Constitution, IPC/BNS & Legal Aid",
                                    icon = Icons.Default.VerifiedUser,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f).testTag("nav_legal_feature"),
                                    onClick = onNavigateToLegal
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Latest PIB Fact-Checks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable(onClick = onNavigateToNews)
                                    .testTag("view_all_news_button")
                            )
                        }

                        if (news.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else {
                            news.filter { it.isFactCheck }.take(3).forEach { article ->
                                NewsCard(
                                    article = article,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("news_card_${article.id}"),
                                    onClick = onNavigateToNews
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("app_disclaimer_card_dp"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
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
                                    text = "CivicLens AI is an independent, non-partisan, public-interest portal powered by advanced artificial intelligence. Our candidate tracking, PIB analysis, and eligibility mapping models rely on public information synthesized via secure RAG (Retrieval-Augmented Generation) and Live Search Grounding.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Key Reference Authorities:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• Press Information Bureau (PIB)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("• Election Commission of India (ECI)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• National Portal of India (india.gov.in)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("• Respective Union Ministry Portals", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Note: While we enforce rigorous multi-source cross-verification algorithms, AI outputs are generated dynamically. Users are strongly encouraged to consult official publications for formal, legally binding decisions or official electoral registration purposes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // MOBILE COMPACT STACK (Original layout)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    PoliticalSearchInput(
                        onInvestigate = { query ->
                            viewModel.updateSearchQuery(query)
                            viewModel.askAssistant(query)
                            onNavigateToAssistant()
                        },
                        placeholderText = "Type a claim or political topic (e.g., 'EVM tampering')..."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (searchHistory.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Recent Fact-Checks & Comparisons",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Clear All",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.clearSearchHistory() }
                                            .testTag("clear_search_history_button")
                                            .padding(4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(searchHistory.take(10)) { item ->
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    viewModel.updateSearchQuery(item.query)
                                                    viewModel.askAssistant(item.query)
                                                    onNavigateToAssistant()
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .testTag("recent_query_${item.id}"),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item.query,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("live_dashboard_card")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("LIVE TRACKER", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    "Lok Sabha 2026",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SeatsChart(
                                title = "General Seats Distribution Projection",
                                parties = listOf(
                                    "NDA / Alliance" to 292,
                                    "INDIA Alliance" to 234,
                                    "Regional/Others" to 17
                                ),
                                totalSeats = 543
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Text(
                            text = "Primary Modules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "AI Assistant",
                                    subtitle = "RAG Verification Chat",
                                    icon = Icons.Default.AutoAwesome,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_assistant_feature"),
                                    onClick = onNavigateToAssistant
                                )
                                FeatureButton(
                                    title = "Compare Hub",
                                    subtitle = "Compare Candidates",
                                    icon = Icons.AutoMirrored.Filled.CompareArrows,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_compare_feature"),
                                    onClick = onNavigateToCompare
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "Scheme Finder",
                                    subtitle = "Factual Eligibility",
                                    icon = Icons.Default.Gavel,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_schemes_feature"),
                                    onClick = onNavigateToSchemes
                                )
                                FeatureButton(
                                    title = "Constituency",
                                    subtitle = "Progress Dashboard",
                                    icon = Icons.Default.Map,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_constituency_feature"),
                                    onClick = onNavigateToConstituency
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "PIB Fact Checks",
                                    subtitle = "Verify Claims",
                                    icon = Icons.AutoMirrored.Filled.FactCheck,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_news_feature"),
                                    onClick = onNavigateToNews
                                )
                                FeatureButton(
                                    title = "Deep Research",
                                    subtitle = "Intensive Summaries",
                                    icon = Icons.Default.BarChart,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_research_feature"),
                                    onClick = onNavigateToResearch
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                FeatureButton(
                                    title = "Speech Sentiment Mapping",
                                    subtitle = "Compare Candidate Veracity",
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_sentiment_feature"),
                                    onClick = onNavigateToSentiment
                                )
                                FeatureButton(
                                    title = "Know Your Rights",
                                    subtitle = "Constitution, IPC/BNS & Legal Aid",
                                    icon = Icons.Default.VerifiedUser,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("nav_legal_feature"),
                                    onClick = onNavigateToLegal
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Latest PIB Fact-Checks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View All",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable(onClick = onNavigateToNews)
                                    .testTag("view_all_news_button")
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (news.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    } else {
                        items(news.filter { it.isFactCheck }) { article ->
                            NewsCard(
                                article = article,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .testTag("news_card_${article.id}"),
                                onClick = onNavigateToNews
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("app_disclaimer_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
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
                                    text = "CivicLens AI is an independent, non-partisan, public-interest portal powered by advanced artificial intelligence. Our candidate tracking, PIB analysis, and eligibility mapping models rely on public information synthesized via secure RAG (Retrieval-Augmented Generation) and Live Search Grounding.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Key Reference Authorities:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• Press Information Bureau (PIB)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("• Election Commission of India (ECI)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("• National Portal of India (india.gov.in)", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("• Respective Union Ministry Portals", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Note: While we enforce rigorous multi-source cross-verification algorithms, AI outputs are generated dynamically. Users are strongly encouraged to consult official publications for formal, legally binding decisions or official electoral registration purposes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    article: DbVerifiedNews,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (article.factCheckVerdict == "FALSE") Color(0xFFFFEBEE) else Color(
                            0xFFE8F5E9
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (article.factCheckVerdict == "FALSE") Icons.Default.Error else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (article.factCheckVerdict == "FALSE") Color(0xFFC62828) else Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = article.date,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
