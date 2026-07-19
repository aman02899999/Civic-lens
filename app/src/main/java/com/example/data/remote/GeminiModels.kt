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
