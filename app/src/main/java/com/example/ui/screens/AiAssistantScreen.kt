package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.local.DbChatMessage
import com.example.data.repository.RagResponse
import com.example.ui.components.GlassCard
import com.example.ui.components.SourceCredibilityChart
import com.example.ui.components.VerifiedFactCard
import com.example.ui.components.CivicLensErrorDisplay
import com.example.ui.components.ErrorType
import com.example.viewmodel.CivicLensViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: CivicLensViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isResponseLoading by viewModel.isResponseLoading.collectAsState()
    val voiceTranscript by viewModel.voiceTranscript.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var isThinkingMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Ensure the general civic session is active (e.g. if the user was previously in the Legal AI consult tab)
    LaunchedEffect(Unit) {
        viewModel.setChatSession("general")
    }

    // Scroll to latest message on update
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CivicLens AI Assistant",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "RAG Verified Grounding Engine",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    // Export PDF Report simulation
                    IconButton(
                        onClick = {
                            val textToExport = chatMessages.joinToString("\n\n") { msg ->
                                "${if (msg.isUser) "USER" else "CIVICLENS AI"}: ${msg.text}"
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CivicLens AI Verified Fact Report")
                                putExtra(Intent.EXTRA_TEXT, textToExport)
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Report"))
                        },
                        modifier = Modifier.testTag("export_report_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Report", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Session", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                // High Thinking & Options Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = isThinkingMode,
                            onCheckedChange = { isThinkingMode = it },
                            modifier = Modifier
                                .scale(0.8f)
                                .testTag("thinking_mode_switch")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Enable High Thinking (Gemini Pro)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isThinkingMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = "Search Grounded",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Bar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Voice input simulator
                    IconButton(
                        onClick = {
                            // Simulate recording & transcribing standard query
                            val simulationQueries = listOf(
                                "What is Narendra Modi's declared assets in his ECI affidavit?",
                                "What are the eligibility benefits for PM-KISAN scheme?",
                                "Tell me about the development projects in Varanasi constituency."
                            )
                            viewModel.simulateVoiceSpeech(simulationQueries.random())
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .size(48.dp)
                            .testTag("voice_search_mic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Search",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Ask CivicLens AI...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (textInput.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.askAssistant(textInput, isThinkingMode)
                                        textInput = ""
                                    },
                                    modifier = Modifier.testTag("chat_send_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.Send,
                                        contentDescription = "Send",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (chatMessages.isEmpty()) {
                // Landing / Onboarding State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Neutral Election Companion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CivicLens AI compares manifesto claims, validates affidavit declarations, and references government press announcements utilizing real-time Google search grounding.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Quick Action Suggestions
                    Text("TAP A SUGGESTED INQUIRY:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val suggestions = listOf(
                        "Verify BJP vs INC job manifesto statements",
                        "Compare assets declared by Narendra Modi vs Rahul Gandhi",
                        "What is PM Jan Arogya eligibility?"
                    )
                    
                    suggestions.forEach { suggestion ->
                        Card(
                            onClick = {
                                textInput = suggestion
                                viewModel.askAssistant(suggestion, isThinkingMode)
                                textInput = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(suggestion, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    if (searchHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "YOUR RECENT INQUIRIES:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Clear",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .clickable { viewModel.clearSearchHistory() }
                                    .testTag("clear_assistant_history_button")
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        searchHistory.take(4).forEach { historyItem ->
                            Card(
                                onClick = {
                                    textInput = historyItem.query
                                    viewModel.askAssistant(historyItem.query, isThinkingMode)
                                    textInput = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("recent_assistant_query_${historyItem.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = historyItem.query,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Messaging Stream
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
                                    title = "AI Verification: ${message.text.take(30)}...",
                                    type = "Chat",
                                    itemId = message.id.toString(),
                                    currentlyBookmarked = isBookmarked
                                )
                            },
                            onRetry = {
                                // Find last user message
                                val lastUserQuery = chatMessages.lastOrNull { it.isUser }?.text
                                if (lastUserQuery != null) {
                                    viewModel.askAssistant(lastUserQuery, isThinkingMode = isThinkingMode)
                                }
                            },
                            onBrowseOffline = onNavigateBack
                        )
                    }

                    if (isResponseLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isThinkingMode) "Gemini is performing deep reasoning..." else "Searching official sources...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageRow(
    message: DbChatMessage,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onRetry: () -> Unit,
    onBrowseOffline: () -> Unit
) {
    if (message.isUser) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(14.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    } else {
        val isOffline = message.text.contains("offline", ignoreCase = true) || 
                        message.text.contains("unable to reach", ignoreCase = true) ||
                        message.text.contains("please verify your internet connection", ignoreCase = true)
        
        val isConfigError = message.text.contains("API key", ignoreCase = true) ||
                            message.text.contains("unauthorized", ignoreCase = true) ||
                            message.text.contains("credentials", ignoreCase = true)
                            
        val isEmptyResult = message.text.contains("no verified parameters matching your query", ignoreCase = true) ||
                            message.text.contains("No grounded records found", ignoreCase = true) ||
                            message.text.isBlank()

        if (isOffline || isConfigError || isEmptyResult) {
            val errorType = when {
                isConfigError -> ErrorType.CONFIG_ERROR
                isOffline -> ErrorType.OFFLINE
                else -> ErrorType.EMPTY_RESULTS
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CivicLensErrorDisplay(
                    errorType = errorType,
                    errorMessage = if (message.text.length > 50 && !message.text.startsWith("###")) message.text else null,
                    onRetry = onRetry,
                    onBrowseOffline = onBrowseOffline
                )
            }
        } else {
            // AI response rendered via VerifiedFactCard
            val ragResponse = RagResponse(
                summary = message.text,
                confidenceScore = message.confidenceScore ?: 0.88,
                sourceCount = message.sourceCount ?: 1,
                lastUpdated = message.lastUpdated ?: "",
                officialSources = message.officialSources ?: emptyList()
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VerifiedFactCard(
                    response = ragResponse,
                    topicQuery = "", // User already sees query bubble in chat history
                    isBookmarked = isBookmarked,
                    onBookmarkToggle = onBookmarkToggle
                )
                if (ragResponse.officialSources.isNotEmpty() && !ragResponse.officialSources.contains("Local Room Encrypted Storage (Offline first)")) {
                    SourceCredibilityChart(
                        response = ragResponse,
                        title = "Grounding Credibility Breakdown"
                    )
                }
            }
        }
    }
}
