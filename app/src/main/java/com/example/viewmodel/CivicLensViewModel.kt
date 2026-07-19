package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.*
import com.example.data.remote.StatementVerificationResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.data.repository.CivicLensRepository
import com.example.data.repository.RagResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class CivicLensViewModel(
    application: Application,
    private val repository: CivicLensRepository
) : AndroidViewModel(application) {

    // Seeding and Initial Setup State
    private val _isSeeding = MutableStateFlow(true)
    val isSeeding: StateFlow<Boolean> = _isSeeding.asStateFlow()

    // Screen State / Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // DB Observables
    val parties: StateFlow<List<DbPoliticalParty>> = repository.allParties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val candidates: StateFlow<List<DbCandidate>> = repository.allCandidates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val constituencies: StateFlow<List<DbConstituency>> = repository.allConstituencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schemes: StateFlow<List<DbGovernmentScheme>> = repository.allSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val news: StateFlow<List<DbVerifiedNews>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<DbBookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-selected comparison states for navigation from Bookmarks
    private val _preselectedCandidates = MutableStateFlow<Pair<String, String>?>(null)
    val preselectedCandidates: StateFlow<Pair<String, String>?> = _preselectedCandidates.asStateFlow()

    private val _preselectedParties = MutableStateFlow<Pair<String, String>?>(null)
    val preselectedParties: StateFlow<Pair<String, String>?> = _preselectedParties.asStateFlow()

    fun setPreselectedCandidates(c1Id: String, c2Id: String) {
        _preselectedCandidates.value = Pair(c1Id, c2Id)
    }

    fun clearPreselectedCandidates() {
        _preselectedCandidates.value = null
    }

    fun setPreselectedParties(p1Id: String, p2Id: String) {
        _preselectedParties.value = Pair(p1Id, p2Id)
    }

    fun clearPreselectedParties() {
        _preselectedParties.value = null
    }

    val searchHistory: StateFlow<List<DbSearchHistory>> = repository.searchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Session name (default is "general")
    private val _chatSessionName = MutableStateFlow("general")
    val chatSessionName: StateFlow<String> = _chatSessionName.asStateFlow()

    val chatMessages: StateFlow<List<DbChatMessage>> = _chatSessionName
        .flatMapLatest { session -> repository.getChatMessages(session) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Loading / Response State
    private val _isResponseLoading = MutableStateFlow(false)
    val isResponseLoading: StateFlow<Boolean> = _isResponseLoading.asStateFlow()

    private val _lastRagResponse = MutableStateFlow<RagResponse?>(null)
    val lastRagResponse: StateFlow<RagResponse?> = _lastRagResponse.asStateFlow()

    // Multi-Language State (Default English)
    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Active UI Styling Template State
    private val _currentTemplate = MutableStateFlow("Classic Glassmorphism")
    val currentTemplate: StateFlow<String> = _currentTemplate.asStateFlow()

    // Voice Recording State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _voiceTranscript = MutableStateFlow("")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.checkAndSeedDatabase()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSeeding.value = false
            }
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setTemplate(template: String) {
        _currentTemplate.value = template
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isNotBlank()) {
                repository.insertSearchQuery(query)
            }
        }
    }

    // --- Bookmark Interactions ---
    fun toggleBookmark(id: String, title: String, type: String, itemId: String, currentlyBookmarked: Boolean) {
        viewModelScope.launch {
            if (currentlyBookmarked) {
                repository.removeBookmark(id)
            } else {
                repository.addBookmark(id, title, type, itemId)
            }
        }
    }

    // --- AI Assistant RAG Query ---
    fun askAssistant(query: String, isThinkingMode: Boolean = false, domain: String = "civic") {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isResponseLoading.value = true
            // Save search history
            repository.insertSearchQuery(query)
            // Save user message to database
            repository.addChatMessage(_chatSessionName.value, isUser = true, text = query)

            // Execute RAG Search
            val response = repository.executeRagQuery(query, isThinkingMode, domain)
            _lastRagResponse.value = response

            // Save AI message to database
            repository.addChatMessage(_chatSessionName.value, isUser = false, text = response.summary, ragResponse = response)
            _isResponseLoading.value = false
        }
    }

    // --- Switch the active chat session (e.g. "general" civic chat vs "legal" rights consultation) ---
    fun setChatSession(sessionName: String) {
        _chatSessionName.value = sessionName
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatSession(_chatSessionName.value)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // --- Transcribe Audio with Gemini 3.5-flash ---
    fun transcribeVoiceAudio(audioFile: File) {
        viewModelScope.launch {
            _isResponseLoading.value = true
            try {
                // Read bytes, encode to Base64
                val bytes = audioFile.readBytes()
                val base64Data = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                
                val promptText = "Please accurately transcribe this election and civic query audio. Only return the final text transcript, nothing else."
                val systemInstructionText = "You are an expert audio transcriber. Return clean, un-punctuated text query."

                val apiKey = BuildConfig.GEMINI_API_KEY
                
                val request = com.example.data.remote.GenerateContentRequest(
                    contents = listOf(
                        com.example.data.remote.Content(
                            parts = listOf(
                                com.example.data.remote.Part(text = promptText),
                                com.example.data.remote.Part(inlineData = com.example.data.remote.InlineData(mimeType = "audio/wav", data = base64Data))
                            )
                        )
                    ),
                    systemInstruction = com.example.data.remote.Content(parts = listOf(com.example.data.remote.Part(text = systemInstructionText)))
                )

                val response = com.example.data.remote.RetrofitClient.geminiService.generateContent(
                    model = "gemini-3.5-flash",
                    apiKey = apiKey,
                    request = request
                )

                val transcript = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: ""
                if (transcript.isNotEmpty()) {
                    _voiceTranscript.value = transcript
                    // Execute query
                    askAssistant(transcript)
                } else {
                    _voiceTranscript.value = "Sorry, could not transcribe audio clearly."
                }
            } catch (e: Exception) {
                _voiceTranscript.value = "Voice transcription failed: ${e.localizedMessage}"
            } finally {
                _isResponseLoading.value = false
            }
        }
    }

    // --- Mock audio simulation for web streaming / testing environments ---
    fun simulateVoiceSpeech(simulatedText: String) {
        viewModelScope.launch {
            _isResponseLoading.value = true
            _voiceTranscript.value = simulatedText
            askAssistant(simulatedText)
            _isResponseLoading.value = false
        }
    }

    // --- Statement Verification Side-by-Side with Gemini API ---
    private val _statementVerificationState = MutableStateFlow<StatementVerificationResult?>(null)
    val statementVerificationState: StateFlow<StatementVerificationResult?> = _statementVerificationState.asStateFlow()

    private val _isVerificationLoading = MutableStateFlow(false)
    val isVerificationLoading: StateFlow<Boolean> = _isVerificationLoading.asStateFlow()

    private val _verificationError = MutableStateFlow<String?>(null)
    val verificationError: StateFlow<String?> = _verificationError.asStateFlow()

    fun verifyStatement(candidateName: String, partyName: String, statement: String) {
        if (statement.isBlank()) return
        _isVerificationLoading.value = true
        _verificationError.value = null
        _statementVerificationState.value = null
        
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                // Detailed prompt instructing Gemini to analyze the candidate's statement non-partisanly
                val systemInstructionText = """
                    You are an expert fact-checker working for a non-partisan civic engagement platform.
                    Your goal is to compare a candidate's statement side-by-side with official data and verified fact-check sources.
                    You MUST return a JSON object with the following schema:
                    {
                      "statement": "the original statement to verify",
                      "candidateName": "name of the candidate",
                      "partyName": "name of the party",
                      "verdict": "TRUE" or "FALSE" or "MISLEADING" or "PARTIALLY_TRUE" or "UNVERIFIED",
                      "factCheckSource": "Name of the official source or fact checking body",
                      "sourceUrl": "The exact URL or domain referencing the verified data",
                      "explanation": "A concise, objective 2-3 sentence explanation of the facts",
                      "confidenceScore": 0.0 to 1.0,
                      "groundingPoints": ["Point 1 of specific factual data comparing the claim with official statistics", "Point 2 of specific factual data comparing the claim with official statistics"]
                    }
                    Be absolutely objective, neutral, and accurate. Support all analysis with real-world public data or ECI records.
                """.trimIndent()

                val promptText = "Compare the following statement by candidate $candidateName of party $partyName side-by-side with verified official records:\n\"$statement\""

                // Active Search Grounding by setting GoogleSearch tool
                val searchTool = com.example.data.remote.Tool(googleSearch = com.example.data.remote.GoogleSearchTool())

                val config = com.example.data.remote.GenerationConfig(
                    temperature = 0.1f,
                    responseFormat = com.example.data.remote.ResponseFormat(type = "application/json")
                )

                val request = com.example.data.remote.GenerateContentRequest(
                    contents = listOf(
                        com.example.data.remote.Content(
                            parts = listOf(com.example.data.remote.Part(text = promptText))
                        )
                    ),
                    generationConfig = config,
                    tools = listOf(searchTool),
                    systemInstruction = com.example.data.remote.Content(
                        parts = listOf(com.example.data.remote.Part(text = systemInstructionText))
                    )
                )

                val response = com.example.data.remote.RetrofitClient.geminiService.generateContent(
                    model = "gemini-3.5-flash",
                    apiKey = apiKey,
                    request = request
                )

                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                
                if (responseText.isNotEmpty()) {
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val adapter = moshi.adapter(StatementVerificationResult::class.java)
                    val parsed = adapter.fromJson(responseText)
                    if (parsed != null) {
                        _statementVerificationState.value = parsed
                    } else {
                        throw Exception("Failed to parse verification response JSON")
                    }
                } else {
                    throw Exception("No response received from verification service")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Local Fallback if offline
                val fallbackResult = StatementVerificationResult(
                    statement = statement,
                    candidateName = candidateName,
                    partyName = partyName,
                    verdict = "UNVERIFIED",
                    factCheckSource = "Local Civic Database",
                    sourceUrl = "https://eci.gov.in",
                    explanation = "We are currently offline or unable to reach the real-time AI fact-checking servers. Please verify your internet connection. Saved details regarding other candidate statistics can be viewed offline.",
                    confidenceScore = 0.50,
                    groundingPoints = listOf(
                        "Please check internet connection to retrieve real-time ECI and PIB verified facts.",
                        "Verify with official PIB Fact Check or ECI press releases when online."
                    )
                )
                _statementVerificationState.value = fallbackResult
                _verificationError.value = e.localizedMessage
            } finally {
                _isVerificationLoading.value = false
            }
        }
    }

    // --- Live Grounded News states ---
    private val _isLiveNewsLoading = MutableStateFlow(false)
    val isLiveNewsLoading: StateFlow<Boolean> = _isLiveNewsLoading.asStateFlow()

    private val _liveNewsError = MutableStateFlow<String?>(null)
    val liveNewsError: StateFlow<String?> = _liveNewsError.asStateFlow()

    fun refreshLiveNews() {
        _isLiveNewsLoading.value = true
        _liveNewsError.value = null
        viewModelScope.launch {
            try {
                val result = repository.fetchLiveNewsFeed()
                if (result.isEmpty()) {
                    _liveNewsError.value = "Unable to fetch live political news. Please check your internet connection and verify if the Gemini API key is active."
                }
            } catch (e: Exception) {
                _liveNewsError.value = "Error fetching live news: ${e.localizedMessage}"
            } finally {
                _isLiveNewsLoading.value = false
            }
        }
    }
}
