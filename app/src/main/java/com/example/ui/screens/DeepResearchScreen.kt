package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RagResponse
import com.example.ui.components.GlassCard
import com.example.ui.components.SourceCredibilityChart
import com.example.ui.components.VerifiedFactCard
import com.example.ui.components.CivicLensErrorDisplay
import com.example.ui.components.ErrorType
import com.example.viewmodel.CivicLensViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepResearchScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bookmarks by viewModel.bookmarks.collectAsState()

    var queryInput by remember { mutableStateOf("") }
    var researchLoading by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var finalResult by remember { mutableStateOf<RagResponse?>(null) }

    val researchSteps = listOf(
        "Scanning Press Information Bureau (PIB) database...",
        "Querying Lok Sabha & Rajya Sabha debate transcripts...",
        "Validating budget allocations against ministerial audit documents...",
        "Ranking references & synthesizing neutral analytical briefing..."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Deep Research", fontWeight = FontWeight.Bold)
                        Text("High-Intelligence Policy Synthesis", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("research_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (finalResult != null) {
                        IconButton(
                            onClick = {
                                val textToExport = finalResult!!.summary
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "CivicLens AI Deep Research Briefing")
                                    putExtra(Intent.EXTRA_TEXT, textToExport)
                                }
                                context.startActivity(Intent.createChooser(intent, "Export PDF Report"))
                            },
                            modifier = Modifier.testTag("research_export_pdf_button")
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Report", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Research Header Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Complex Inquiry Portal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter highly complex policy topics. CivicLens uses Gemini Pro with full reasoning logic to construct comparative balance matrices. All summaries are completely neutral and source-cited.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = queryInput,
                        onValueChange = { queryInput = it },
                        placeholder = { Text("E.g., Analyze agricultural water subsidy models across northern states and list associated PIB clarifications.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("research_query_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            if (queryInput.isNotBlank()) {
                                researchLoading = true
                                currentStepIndex = 0
                                finalResult = null
                                
                                scope.launch {
                                    // Step animation simulation
                                    while (currentStepIndex < researchSteps.size - 1) {
                                        delay(1500)
                                        currentStepIndex++
                                    }
                                }
                                
                                scope.launch {
                                    try {
                                        viewModel.askAssistant(queryInput, isThinkingMode = true)
                                        
                                        // Observe results
                                        while (viewModel.isResponseLoading.value) {
                                            delay(500)
                                        }
                                        finalResult = viewModel.lastRagResponse.value
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        researchLoading = false
                                        currentStepIndex = researchSteps.size - 1
                                    }
                                }
                            }
                        },
                        enabled = !researchLoading && queryInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_research_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Initiate Deep AI Research", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Research steps Checklist
            if (researchLoading) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth().testTag("research_checklist_card")) {
                        Text(
                            text = "Research Progress Matrix",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        researchSteps.forEachIndexed { index, step ->
                            val isCompleted = index < currentStepIndex
                            val isActive = index == currentStepIndex
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isActive) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Pending",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = step,
                                    fontSize = 12.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Report Output Section
            if (finalResult != null) {
                val res = finalResult!!
                val isOffline = res.summary.contains("offline", ignoreCase = true) || 
                                res.summary.contains("unable to reach", ignoreCase = true) ||
                                res.summary.contains("please verify your internet connection", ignoreCase = true)
                
                val isConfigError = res.summary.contains("API key", ignoreCase = true) ||
                                    res.summary.contains("unauthorized", ignoreCase = true) ||
                                    res.summary.contains("credentials", ignoreCase = true)
                                    
                val isEmptyResult = res.summary.contains("no verified parameters matching your query", ignoreCase = true) ||
                                    res.summary.contains("No grounded records found", ignoreCase = true) ||
                                    res.summary.isBlank()

                if (isOffline || isConfigError || isEmptyResult) {
                    val errorType = when {
                        isConfigError -> ErrorType.CONFIG_ERROR
                        isOffline -> ErrorType.OFFLINE
                        else -> ErrorType.EMPTY_RESULTS
                    }
                    item {
                        CivicLensErrorDisplay(
                            errorType = errorType,
                            errorMessage = if (res.summary.length > 50 && !res.summary.startsWith("###")) res.summary else null,
                            onRetry = {
                                if (queryInput.isNotBlank()) {
                                    researchLoading = true
                                    currentStepIndex = 0
                                    finalResult = null
                                    scope.launch {
                                        while (currentStepIndex < researchSteps.size - 1) {
                                            delay(1500)
                                            currentStepIndex++
                                        }
                                    }
                                    scope.launch {
                                        try {
                                            viewModel.askAssistant(queryInput, isThinkingMode = true)
                                            while (viewModel.isResponseLoading.value) {
                                                delay(500)
                                            }
                                            finalResult = viewModel.lastRagResponse.value
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        } finally {
                                            researchLoading = false
                                            currentStepIndex = researchSteps.size - 1
                                        }
                                    }
                                }
                            },
                            onBrowseOffline = onNavigateBack,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                } else {
                    val isBookmarked = bookmarks.any { it.id == "research_${queryInput}" }
                    item {
                        VerifiedFactCard(
                            response = res,
                            topicQuery = queryInput,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = {
                                viewModel.toggleBookmark(
                                    id = "research_${queryInput}",
                                    title = "Research: $queryInput",
                                    type = "Research",
                                    itemId = queryInput,
                                    currentlyBookmarked = isBookmarked
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SourceCredibilityChart(
                            response = res,
                            title = "Research Source Credibility Index"
                        )
                    }
                }
            }
        }
    }
}
