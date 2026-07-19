package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.legal.ConstitutionArticle
import com.example.data.legal.LandmarkCase
import com.example.data.legal.LawSection
import com.example.data.legal.LegalKnowledgeBase
import com.example.data.legal.RightsTopic
import com.example.ui.components.GlassCard
import com.example.viewmodel.CivicLensViewModel
import kotlinx.coroutines.launch

private const val LEGAL_CHAT_SESSION = "legal"

const val LEGAL_TAB_CONSTITUTION = 0
const val LEGAL_TAB_CRIMINAL_LAW = 1
const val LEGAL_TAB_RIGHTS_TOPICS = 2
const val LEGAL_TAB_AI_CONSULT = 3
const val LEGAL_TAB_LANDMARK_CASES = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalRightsScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(LEGAL_TAB_CONSTITUTION) }
    val preselectedLegalTab by viewModel.preselectedLegalTab.collectAsState()

    LaunchedEffect(preselectedLegalTab) {
        preselectedLegalTab?.let { tab ->
            selectedTab = tab
            viewModel.clearPreselectedLegalTab()
        }
    }

    LaunchedEffect(selectedTab) {
        viewModel.setChatSession(if (selectedTab == LEGAL_TAB_AI_CONSULT) LEGAL_CHAT_SESSION else "general")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Know Your Rights", fontWeight = FontWeight.Bold)
                        Text(
                            "Constitution • Criminal Law • Legal Aid",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("legal_back_button")) {
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
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 8.dp) {
                Tab(
                    selected = selectedTab == LEGAL_TAB_CONSTITUTION,
                    onClick = { selectedTab = LEGAL_TAB_CONSTITUTION },
                    text = { Text("Constitution", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("constitution_tab")
                )
                Tab(
                    selected = selectedTab == LEGAL_TAB_CRIMINAL_LAW,
                    onClick = { selectedTab = LEGAL_TAB_CRIMINAL_LAW },
                    text = { Text("IPC → BNS", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("criminal_law_tab")
                )
                Tab(
                    selected = selectedTab == LEGAL_TAB_RIGHTS_TOPICS,
                    onClick = { selectedTab = LEGAL_TAB_RIGHTS_TOPICS },
                    text = { Text("Know Your Rights", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("rights_topics_tab")
                )
                Tab(
                    selected = selectedTab == LEGAL_TAB_LANDMARK_CASES,
                    onClick = { selectedTab = LEGAL_TAB_LANDMARK_CASES },
                    text = { Text("Landmark Cases", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("landmark_cases_tab")
                )
                Tab(
                    selected = selectedTab == LEGAL_TAB_AI_CONSULT,
                    onClick = { selectedTab = LEGAL_TAB_AI_CONSULT },
                    text = { Text("AI Legal Consult", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("legal_ai_tab")
                )
            }

            when (selectedTab) {
                LEGAL_TAB_CONSTITUTION -> ConstitutionTab(viewModel = viewModel)
                LEGAL_TAB_CRIMINAL_LAW -> CriminalLawTab(viewModel = viewModel)
                LEGAL_TAB_RIGHTS_TOPICS -> RightsTopicsTab(viewModel = viewModel)
                LEGAL_TAB_LANDMARK_CASES -> LandmarkCasesTab(viewModel = viewModel)
                LEGAL_TAB_AI_CONSULT -> LegalAiConsultTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LegalDisclaimerBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Educational reference only, not a substitute for advice from a licensed advocate. Always verify section numbers against the official Bare Act (India Code) for your specific case.",
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ConstitutionTab(viewModel: CivicLensViewModel) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LegalKnowledgeBase.constitutionCategoryAll) }

    val filtered = LegalKnowledgeBase.constitutionArticles.filter { article ->
        val matchesSearch = article.title.contains(searchQuery, ignoreCase = true) ||
                article.articleNumber.contains(searchQuery, ignoreCase = true) ||
                article.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == LegalKnowledgeBase.constitutionCategoryAll ||
                article.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search articles, e.g. \"Article 21\", \"equality\"...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("constitution_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LegalKnowledgeBase.constitutionCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("constitution_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LegalDisclaimerBanner() }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No matching Constitutional provisions found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filtered) { article ->
                    val bookmarkId = "legal_article_${article.articleNumber}"
                    val isBookmarked = bookmarks.any { it.id == bookmarkId }
                    ConstitutionArticleCard(
                        article = article,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = bookmarkId,
                                title = "${article.articleNumber}: ${article.title}",
                                type = "legal_article",
                                itemId = article.articleNumber,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        modifier = Modifier.testTag("article_card_${article.articleNumber}")
                    )
                }
            }
        }
    }
}

@Composable
private fun ConstitutionArticleCard(
    article: ConstitutionArticle,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier.clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = article.articleNumber.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_article_${article.articleNumber}")) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark Article",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CriminalLawTab(viewModel: CivicLensViewModel) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = LegalKnowledgeBase.criminalLawSections.filter { section ->
        val matchesSearch = section.title.contains(searchQuery, ignoreCase = true) ||
                section.oldIpcSection.contains(searchQuery, ignoreCase = true) ||
                section.newBnsSection.contains(searchQuery, ignoreCase = true) ||
                section.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || section.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search offence, IPC section, or BNS section...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("criminal_law_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LegalKnowledgeBase.criminalLawCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("criminal_law_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LegalDisclaimerBanner() }
            item {
                Text(
                    text = "The Bharatiya Nyaya Sanhita (BNS) 2023 replaced the Indian Penal Code (IPC) 1860, effective 1 July 2024. Old IPC numbers are shown for reference alongside the current BNS section in force.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No matching criminal law sections found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filtered) { section ->
                    val bookmarkId = "legal_law_${section.newBnsSection}"
                    val isBookmarked = bookmarks.any { it.id == bookmarkId }
                    LawSectionCard(
                        section = section,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = bookmarkId,
                                title = "${section.title} (BNS ${section.newBnsSection})",
                                type = "legal_law_section",
                                itemId = section.newBnsSection,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        modifier = Modifier.testTag("law_section_card_${section.newBnsSection}")
                    )
                }
            }
        }
    }
}

@Composable
private fun LawSectionCard(
    section: LawSection,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(modifier = modifier.clickable { expanded = !expanded }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("IPC ${section.oldIpcSection}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("BNS ${section.newBnsSection}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_law_${section.newBnsSection}")) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark Section",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = section.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Punishment: ${section.punishment}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun RightsTopicsTab(viewModel: CivicLensViewModel) {
    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filtered = LegalKnowledgeBase.rightsTopics.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.summary.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search rights, e.g. \"arrest\", \"consumer\", \"RTI\"...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("rights_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LegalDisclaimerBanner() }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No matching rights topics found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filtered) { topic ->
                    val bookmarkId = "legal_rights_${topic.title.hashCode()}"
                    val isBookmarked = bookmarks.any { it.id == bookmarkId }
                    RightsTopicCard(
                        topic = topic,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = bookmarkId,
                                title = topic.title,
                                type = "legal_rights_topic",
                                itemId = topic.title,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        onShare = {
                            val shareText = "${topic.title}\n\n${topic.summary}\n\n" +
                                topic.keyPoints.joinToString("\n") { "• $it" } +
                                "\n\n— Shared from CivicLens AI, Know Your Rights"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, topic.title)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share ${topic.title}"))
                        },
                        modifier = Modifier.testTag("rights_topic_card_${topic.title.take(8)}")
                    )
                }
            }
        }
    }
}

@Composable
private fun RightsTopicCard(
    topic: RightsTopic,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(modifier = modifier.clickable { expanded = !expanded }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onShare, modifier = Modifier.testTag("share_rights_${topic.title.take(8)}")) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_rights_${topic.title.take(8)}")) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark Topic",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = topic.summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp
        )

        AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                topic.keyPoints.forEach { point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LandmarkCasesTab(viewModel: CivicLensViewModel) {
    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = LegalKnowledgeBase.landmarkCases.filter { case ->
        val matchesSearch = case.caseName.contains(searchQuery, ignoreCase = true) ||
                case.holding.contains(searchQuery, ignoreCase = true) ||
                case.significance.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || case.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search landmark judgments, e.g. \"privacy\", \"basic structure\"...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("landmark_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(LegalKnowledgeBase.landmarkCaseCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("landmark_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LegalDisclaimerBanner() }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No matching landmark judgments found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filtered) { case ->
                    val bookmarkId = "legal_case_${case.caseName.hashCode()}"
                    val isBookmarked = bookmarks.any { it.id == bookmarkId }
                    LandmarkCaseCard(
                        case = case,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = bookmarkId,
                                title = "${case.caseName} (${case.year})",
                                type = "legal_case",
                                itemId = case.caseName,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        onShare = {
                            val shareText = "${case.caseName} (${case.year})\n\n" +
                                "Holding: ${case.holding}\n\n" +
                                "Significance: ${case.significance}\n\n" +
                                "— Shared from CivicLens AI, Know Your Rights"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, case.caseName)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share ${case.caseName}"))
                        },
                        modifier = Modifier.testTag("landmark_case_card_${case.caseName.take(8)}")
                    )
                }
            }
        }
    }
}

@Composable
private fun LandmarkCaseCard(
    case: LandmarkCase,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    GlassCard(modifier = modifier.clickable { expanded = !expanded }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(case.year, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = case.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = case.caseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onShare, modifier = Modifier.testTag("share_case_${case.caseName.take(8)}")) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_case_${case.caseName.take(8)}")) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark Case",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Holding: ${case.holding}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = case.significance,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalAiConsultTab(viewModel: CivicLensViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isResponseLoading by viewModel.isResponseLoading.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(chatMessages.size - 1) }
        }
    }

    val suggestions = listOf(
        "What are my rights if the police arrest me without a warrant?",
        "What is the BNS section for cheating, and what was it called under the old IPC?",
        "How do I file an RTI application and what is the response deadline?",
        "What protections do I have under the Domestic Violence Act?"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (chatMessages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Legal AI",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ask CivicLens Legal AI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Get plain-language answers grounded in the Constitution of India, the BNS/BNSS, and other key statutes — with real-time search grounding for accuracy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("TAP A SAMPLE QUESTION:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    suggestions.forEach { suggestion ->
                        Card(
                            onClick = {
                                viewModel.askAssistant(suggestion, domain = "legal")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(suggestion, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LegalDisclaimerBanner()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(chatMessages) { message ->
                        val isBookmarked = bookmarks.any { it.id == "chat_${message.id}" }
                        ChatMessageRow(
                            message = message,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(
                                    id = "chat_${message.id}",
                                    title = "Legal Consult: ${message.text.take(30)}...",
                                    type = "LegalChat",
                                    itemId = message.id.toString(),
                                    currentlyBookmarked = isBookmarked
                                )
                            },
                            onRetry = {
                                val lastUserQuery = chatMessages.lastOrNull { it.isUser }?.text
                                if (lastUserQuery != null) {
                                    viewModel.askAssistant(lastUserQuery, domain = "legal")
                                }
                            },
                            onBrowseOffline = {}
                        )
                    }
                    if (isResponseLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Consulting Constitutional & criminal law sources...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask about your rights, IPC/BNS sections, or the Constitution...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("legal_chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (textInput.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.askAssistant(textInput, domain = "legal")
                                    textInput = ""
                                },
                                modifier = Modifier.testTag("legal_chat_send_button")
                            ) {
                                Icon(Icons.AutoMirrored.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                )
            }
        }
    }
}
