package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbBookmark
import com.example.ui.components.GlassCard
import com.example.viewmodel.CivicLensViewModel

private const val BOOKMARK_CATEGORY_ALL = "All"

/** Groups the raw bookmark `type` values into a friendly, user-facing category for filtering. */
private fun bookmarkCategory(type: String): String = when (type) {
    "legal_article", "legal_law_section", "legal_rights_topic", "legal_case", "LegalChat" -> "Legal & Rights"
    "voter_topic" -> "Voter Toolkit"
    "compare_candidates", "compare_parties" -> "Comparisons"
    "scheme" -> "Schemes"
    "news" -> "News & Fact-Checks"
    "sentiment" -> "Discourse Analysis"
    "Chat" -> "AI Chats"
    else -> "Other"
}

/** Friendly label for the small type badge shown on each bookmark card. */
private fun bookmarkTypeLabel(type: String): String = when (type) {
    "legal_article" -> "Constitution Article"
    "legal_law_section" -> "IPC / BNS Section"
    "legal_rights_topic" -> "Know Your Rights"
    "legal_case" -> "Landmark Case"
    "voter_topic" -> "Voter Guide"
    "LegalChat" -> "Legal AI Chat"
    "Chat" -> "AI Assistant Chat"
    "compare_candidates" -> "Candidate Comparison"
    "compare_parties" -> "Party Comparison"
    "scheme" -> "Government Scheme"
    "news" -> "News / Fact-Check"
    "sentiment" -> "Discourse Analysis"
    else -> type.uppercase()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSchemes: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToCompare: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onNavigateToVoterToolkit: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    var selectedCategory by remember { mutableStateOf(BOOKMARK_CATEGORY_ALL) }

    val availableCategories = remember(bookmarks) {
        listOf(BOOKMARK_CATEGORY_ALL) + bookmarks.map { bookmarkCategory(it.type) }.distinct().sorted()
    }
    // If the selected category's last bookmark was removed, fall back to "All" instead of showing a
    // silently-empty list for a category that's no longer selectable from the chip row.
    LaunchedEffect(availableCategories) {
        if (selectedCategory !in availableCategories) {
            selectedCategory = BOOKMARK_CATEGORY_ALL
        }
    }
    val filteredBookmarks = if (selectedCategory == BOOKMARK_CATEGORY_ALL) {
        bookmarks
    } else {
        bookmarks.filter { bookmarkCategory(it.type) == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Bookmarks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("bookmarks_back_button")) {
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
            if (bookmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No saved bookmarks found.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bookmark government schemes, verified PIB fact checks, comparisons, or Constitution articles, IPC/BNS sections, and landmark judgments from Know Your Rights for quick offline references.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                if (availableCategories.size > 2) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableCategories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontSize = 12.sp) },
                                modifier = Modifier.testTag("bookmark_category_chip_$category")
                            )
                        }
                    }
                }

                if (filteredBookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No bookmarks in \"$selectedCategory\".",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookmarks) { bookmark ->
                        BookmarkRowCard(
                            bookmark = bookmark,
                            onRemove = {
                                viewModel.toggleBookmark(
                                    id = bookmark.id,
                                    title = bookmark.title,
                                    type = bookmark.type,
                                    itemId = bookmark.itemId,
                                    currentlyBookmarked = true
                                )
                            },
                            onOpen = {
                                when (bookmark.type) {
                                    "scheme" -> onNavigateToSchemes()
                                    "news" -> onNavigateToNews()
                                    "compare_candidates" -> {
                                        val parts = bookmark.itemId.split("_")
                                        if (parts.size == 2) {
                                            viewModel.setPreselectedCandidates(parts[0], parts[1])
                                            onNavigateToCompare()
                                        }
                                    }
                                    "compare_parties" -> {
                                        val parts = bookmark.itemId.split("_")
                                        if (parts.size == 2) {
                                            viewModel.setPreselectedParties(parts[0], parts[1])
                                            onNavigateToCompare()
                                        }
                                    }
                                    "Chat" -> onNavigateToAssistant()
                                    "LegalChat" -> {
                                        viewModel.setPreselectedLegalTab(LEGAL_TAB_AI_CONSULT)
                                        onNavigateToLegal()
                                    }
                                    "legal_article" -> {
                                        viewModel.setPreselectedLegalTab(LEGAL_TAB_CONSTITUTION)
                                        onNavigateToLegal()
                                    }
                                    "legal_law_section" -> {
                                        viewModel.setPreselectedLegalTab(LEGAL_TAB_CRIMINAL_LAW)
                                        onNavigateToLegal()
                                    }
                                    "legal_rights_topic" -> {
                                        viewModel.setPreselectedLegalTab(LEGAL_TAB_RIGHTS_TOPICS)
                                        onNavigateToLegal()
                                    }
                                    "legal_case" -> {
                                        viewModel.setPreselectedLegalTab(LEGAL_TAB_LANDMARK_CASES)
                                        onNavigateToLegal()
                                    }
                                    "voter_topic" -> onNavigateToVoterToolkit()
                                    else -> onNavigateToSchemes()
                                }
                            },
                            modifier = Modifier.testTag("bookmark_card_${bookmark.id}")
                        )
                    }
                }
                }
            }
        }
    }
}

@Composable
fun BookmarkRowCard(
    bookmark: DbBookmark,
    onRemove: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bookmarkTypeLabel(bookmark.type).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bookmark.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row {
                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.BookmarkRemove,
                        contentDescription = "Remove Bookmark",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
