package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<Tool>? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class Tool(
    val googleSearch: GoogleSearchTool? = null
)

@JsonClass(generateAdapter = true)
data class GoogleSearchTool(
    val placeholder: String? = null // Empty object {} in JSON, we can represent with optional field
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val thinkingConfig: ThinkingConfig? = null,
    val responseModalities: List<String>? = null,
    val speechConfig: SpeechConfig? = null
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    val type: String? = null, // e.g., "application/json"
    val responseSchema: Map<String, Any>? = null
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    val thinkingLevel: String // "LOW", "MEDIUM", "HIGH"
)

@JsonClass(generateAdapter = true)
data class SpeechConfig(
    val voiceConfig: VoiceConfig
)

@JsonClass(generateAdapter = true)
data class VoiceConfig(
    val prebuiltVoiceConfig: PrebuiltVoiceConfig
)

@JsonClass(generateAdapter = true)
data class PrebuiltVoiceConfig(
    val voiceName: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?,
    val promptFeedback: PromptFeedback?,
    val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?,
    val finishReason: String?,
    val groundingMetadata: GroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GroundingMetadata(
    val groundingChunks: List<GroundingChunk>?,
    val webSearchQueries: List<String>?,
    val searchEntryPoint: SearchEntryPoint?
)

@JsonClass(generateAdapter = true)
data class GroundingChunk(
    val web: WebChunk?
)

@JsonClass(generateAdapter = true)
data class WebChunk(
    val uri: String?,
    val title: String?
)

@JsonClass(generateAdapter = true)
data class SearchEntryPoint(
    val renderedContent: String?
)

@JsonClass(generateAdapter = true)
data class PromptFeedback(
    val blockReason: String?
)

@JsonClass(generateAdapter = true)
data class GeminiError(
    val code: Int?,
    val message: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class StatementVerificationResult(
    val statement: String,
    val candidateName: String,
    val partyName: String,
    val verdict: String,
    val factCheckSource: String,
    val sourceUrl: String,
    val explanation: String,
    val confidenceScore: Double,
    val groundingPoints: List<String>
)

@JsonClass(generateAdapter = true)
data class LiveNewsArticle(
    val title: String,
    val content: String,
    val date: String,
    val source: String,
    val verdict: String,
    val confidenceScore: Double,
    val officialSources: List<String>,
    val originalUrl: String
)

@JsonClass(generateAdapter = true)
data class LiveNewsResponse(
    val articles: List<LiveNewsArticle>
)

@JsonClass(generateAdapter = true)
data class CandidateQueryResponse(
    val query: String,
    val candidateName: String,
    val partyName: String,
    val constituency: String,
    val currentRole: String,
    val education: String,
    val declaredAssets: String,
    val criminalCasesCount: Int,
    val summaryBio: String,
    val keyStancesAndPromises: List<String>,
    val verifiedAchievements: List<String>,
    val controversiesAndFactChecks: List<CandidateFactCheck>,
    val officialCitations: List<CandidateCitation>,
    val confidenceScore: Double
)

@JsonClass(generateAdapter = true)
data class CandidateFactCheck(
    val claimOrIssue: String,
    val verdict: String,
    val explanation: String
)

@JsonClass(generateAdapter = true)
data class CandidateCitation(
    val sourceName: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class GovtJobSearchResponse(
    val lastUpdatedDate: String,
    val totalAlertsFound: Int,
    val jobs: List<GovtJobItem>
)

@JsonClass(generateAdapter = true)
data class GovtJobItem(
    val id: String,
    val title: String,
    val organization: String,
    val category: String,
    val totalVacancies: String,
    val salaryScale: String = "",
    val lastDateToApply: String,
    val eligibilityCriteria: String,
    val ageLimit: String,
    val applicationFee: String,
    val officialPortalName: String,
    val officialApplyUrl: String,
    val whereToApply: String,
    val howToApplySteps: List<String>,
    val requiredDocuments: List<String>,
    val selectionProcess: List<String>,
    val prepGuideSummary: String,
    val prepStrategySteps: List<String>,
    val syllabusOverview: String,
    val isLatestNotification: Boolean = true
)

@JsonClass(generateAdapter = true)
data class RealTimeNewsSource(
    val title: String,
    val publisher: String,
    val url: String,
    val publishedDate: String = "",
    val relevanceSnippet: String = ""
)

@JsonClass(generateAdapter = true)
data class ClaimEvidencePoint(
    val pointTitle: String,
    val evidenceDetail: String,
    val sourceName: String
)

@JsonClass(generateAdapter = true)
data class ElectionClaimVerificationResult(
    val claimText: String,
    val verdict: String, // e.g. "VERIFIED TRUE", "DEBUNKED FALSE", "MISLEADING", "PARTIALLY TRUE", "UNVERIFIED"
    val verdictSummary: String,
    val confidenceScore: Double,
    val truthScorePercent: Int,
    val claimContext: String,
    val factCheckBreakdown: String,
    val keyEvidencePoints: List<ClaimEvidencePoint>,
    val realTimeNewsSources: List<RealTimeNewsSource>,
    val officialPortalsChecked: List<String>,
    val recommendedClarification: String,
    val timestamp: String = ""
)


