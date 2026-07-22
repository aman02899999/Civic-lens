package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.civic.CivicHelpline
import com.example.data.civic.VoterKnowledgeBase
import com.example.data.civic.VoterTopic
import com.example.ui.components.GlassCard
import com.example.viewmodel.CivicLensViewModel

const val VOTER_TAB_GUIDE = 0
const val VOTER_TAB_HELPLINES = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoterToolkitScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(VOTER_TAB_GUIDE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Voter Toolkit", fontWeight = FontWeight.Bold)
                        Text(
                            "Registration • Voting Day • Helplines",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("voter_back_button")) {
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
                    selected = selectedTab == VOTER_TAB_GUIDE,
                    onClick = { selectedTab = VOTER_TAB_GUIDE },
                    text = { Text("Voter Guide", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("voter_guide_tab")
                )
                Tab(
                    selected = selectedTab == VOTER_TAB_HELPLINES,
                    onClick = { selectedTab = VOTER_TAB_HELPLINES },
                    text = { Text("Helplines", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("helplines_tab")
                )
            }

            when (selectedTab) {
                VOTER_TAB_GUIDE -> VoterGuideTab(viewModel = viewModel)
                VOTER_TAB_HELPLINES -> HelplinesTab()
            }
        }
    }
}

@Composable
private fun VoterDisclaimerBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Procedures summarized from official ECI guidance. Always verify current forms, dates, and accepted documents on voters.eci.gov.in or the Voter Helpline app before acting.",
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VoterGuideTab(viewModel: CivicLensViewModel) {
    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = VoterKnowledgeBase.voterTopics.filter { topic ->
        val matchesSearch = topic.title.contains(searchQuery, ignoreCase = true) ||
                topic.summary.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || topic.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search, e.g. \"register\", \"EVM\", \"NOTA\"...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("voter_search_input"),
            shape = RoundedCornerShape(12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(VoterKnowledgeBase.voterTopicCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("voter_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { VoterDisclaimerBanner() }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        "No matching voter guide topics found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(filtered) { topic ->
                    val bookmarkId = "voter_topic_${topic.title.hashCode()}"
                    val isBookmarked = bookmarks.any { it.id == bookmarkId }
                    VoterTopicCard(
                        topic = topic,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            viewModel.toggleBookmark(
                                id = bookmarkId,
                                title = topic.title,
                                type = "voter_topic",
                                itemId = topic.title,
                                currentlyBookmarked = isBookmarked
                            )
                        },
                        onShare = {
                            val shareText = "${topic.title}\n\n${topic.summary}\n\n" +
                                topic.keyPoints.joinToString("\n") { "• $it" } +
                                "\n\n— Shared from CivicLens AI, Voter Toolkit"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, topic.title)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share ${topic.title}"))
                        },
                        modifier = Modifier.testTag("voter_topic_card_${topic.title.take(8)}")
                    )
                }
            }
        }
    }
}

@Composable
private fun VoterTopicCard(
    topic: VoterTopic,
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
            IconButton(onClick = onShare, modifier = Modifier.testTag("share_voter_${topic.title.take(8)}")) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("bookmark_voter_${topic.title.take(8)}")) {
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
private fun HelplinesTab() {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = VoterKnowledgeBase.helplines.filter {
        selectedCategory == "All" || it.category == selectedCategory
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(VoterKnowledgeBase.helplineCategories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    modifier = Modifier.testTag("helpline_chip_$category")
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
                Text(
                    text = "Tap a number to open your dialer — calls are never placed automatically. Numbers are national defaults; some states operate additional local lines.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
            items(filtered) { helpline ->
                HelplineCard(
                    helpline = helpline,
                    onDial = {
                        if (helpline.number.isNotBlank()) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${helpline.number}"))
                            context.startActivity(intent)
                        }
                    },
                    onOpenWeb = {
                        if (helpline.webUrl.isNotBlank()) {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(helpline.webUrl)))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier.testTag("helpline_card_${helpline.name.take(8)}")
                )
            }
        }
    }
}

@Composable
private fun HelplineCard(
    helpline: CivicHelpline,
    onDial: () -> Unit,
    onOpenWeb: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = helpline.category.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = helpline.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = helpline.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (helpline.number.isNotBlank()) {
                    Button(
                        onClick = onDial,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("dial_${helpline.number}")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(helpline.number, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }
                if (helpline.webUrl.isNotBlank()) {
                    TextButton(
                        onClick = onOpenWeb,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("web_${helpline.name.take(8)}")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
