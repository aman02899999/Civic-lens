package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.civic.GovPortalsDirectory
import com.example.data.civic.PortalLink
import com.example.data.local.DbGovernmentScheme
import com.example.ui.components.GlassCard
import com.example.ui.components.ShimmerListCard
import com.example.viewmodel.CivicLensViewModel

const val SCHEMES_TAB_FINDER = 0
const val SCHEMES_TAB_PORTALS = 1
const val SCHEMES_TAB_JOBS = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemesScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(SCHEMES_TAB_FINDER) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Schemes & Govt Jobs", fontWeight = FontWeight.Bold)
                        Text(
                            "Verified Benefits • Official Portals Only",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("schemes_back_button")) {
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == SCHEMES_TAB_FINDER,
                    onClick = { selectedTab = SCHEMES_TAB_FINDER },
                    text = { Text("Scheme Finder", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("scheme_finder_tab")
                )
                Tab(
                    selected = selectedTab == SCHEMES_TAB_PORTALS,
                    onClick = { selectedTab = SCHEMES_TAB_PORTALS },
                    text = { Text("Scheme Portals", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("scheme_portals_tab")
                )
                Tab(
                    selected = selectedTab == SCHEMES_TAB_JOBS,
                    onClick = { selectedTab = SCHEMES_TAB_JOBS },
                    text = { Text("Govt Jobs", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("govt_jobs_tab")
                )
            }

            when (selectedTab) {
                SCHEMES_TAB_FINDER -> SchemeFinderTab(
                    viewModel = viewModel,
                    onNavigateToAssistant = onNavigateToAssistant
                )
                SCHEMES_TAB_PORTALS -> PortalLinksTab(
                    portals = GovPortalsDirectory.schemePortals,
                    categories = GovPortalsDirectory.schemePortalCategories,
                    note = "Official Government of India scheme portals. Applications on these sites are free unless the scheme itself states a fee — never pay agents or middlemen to \"process\" a government benefit.",
                    tagPrefix = "scheme_portal"
                )
                else -> PortalLinksTab(
                    portals = GovPortalsDirectory.jobPortals,
                    categories = GovPortalsDirectory.jobPortalCategories,
                    note = "Official recruitment portals only — government jobs are never sold. Any person or website demanding money to secure a sarkari naukri is running a scam; report it to cyber crime helpline 1930. State government jobs are notified on each state's own Public Service Commission website.",
                    tagPrefix = "job_portal"
                )
            }
        }
    }
}

@Composable
private fun SchemeFinderTab(
    viewModel: CivicLensViewModel,
    onNavigateToAssistant: () -> Unit
) {
    val schemes by viewModel.schemes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val isSeeding by viewModel.isSeeding.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Agriculture", "Health", "Finance", "Urban Development")

    // Filter schemes
    val filteredSchemes = schemes.filter { scheme ->
        val matchesSearch = scheme.name.contains(searchQuery, ignoreCase = true) ||
                scheme.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" ||
                scheme.category.contains(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search schemes, benefits, ministries...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("scheme_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Category Horizontal Chips Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    modifier = Modifier.testTag("chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Schemes List
        if (isSeeding && schemes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        ShimmerListCard()
                    }
                }
            }
        } else if (filteredSchemes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching verified schemes found in database.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSchemes) { scheme ->
                    val isBookmarked = bookmarks.any { it.itemId == scheme.id }
                    SchemeCard(
                        scheme = scheme,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = "scheme_${scheme.id}",
                                title = scheme.name,
                                type = "scheme",
                                itemId = scheme.id,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        onAskAI = {
                            viewModel.askAssistant("Explain application process and eligibility for Scheme: ${scheme.name}")
                            onNavigateToAssistant()
                        },
                        modifier = Modifier.testTag("scheme_card_${scheme.id}")
                    )
                }
            }
        }
    }
}

@Composable
private fun PortalLinksTab(
    portals: List<PortalLink>,
    categories: List<String>,
    note: String,
    tagPrefix: String
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = portals.filter { selectedCategory == "All" || it.category == selectedCategory }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("${tagPrefix}_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(filtered) { portal ->
                PortalLinkCard(
                    portal = portal,
                    onOpen = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(portal.url)))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.testTag("${tagPrefix}_card_${portal.name.take(8)}")
                )
            }
        }
    }
}

@Composable
private fun PortalLinkCard(
    portal: PortalLink,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, onClick = onOpen) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = portal.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = portal.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = portal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = portal.url.removePrefix("https://").removePrefix("http://"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onOpen,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_${portal.name.take(8)}")
            ) {
                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SchemeCard(
    scheme: DbGovernmentScheme,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onAskAI: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scheme.ministry,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = scheme.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark Scheme",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = scheme.description,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Eligibility / Benefit Blocks
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Eligibility: ${scheme.eligibility}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Benefits: ${scheme.benefits}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ask AI button
            Button(
                onClick = onAskAI,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Check Eligibility", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Launch official url
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme.sourceUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Official Website", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
