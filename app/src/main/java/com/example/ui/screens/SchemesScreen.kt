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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DbGovernmentScheme
import com.example.ui.components.GlassCard
import com.example.viewmodel.CivicLensViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemesScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAssistant: () -> Unit
) {
    val schemes by viewModel.schemes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Government Scheme Finder", fontWeight = FontWeight.Bold) },
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
            if (filteredSchemes.isEmpty()) {
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
                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Eligibility: ${scheme.eligibility}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
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
