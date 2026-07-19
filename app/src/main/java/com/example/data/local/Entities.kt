package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "political_parties")
data class DbPoliticalParty(
    @PrimaryKey val id: String,
    val name: String,
    val president: String,
    val founded: String,
    val manifestoSummary: String,
    val officialWebsite: String,
    val voteShareHistory: String, // e.g. "2019: 37.36%, 2014: 31.0%"
    val seatsHistory: String,     // e.g. "2019: 303, 2014: 282"
    val achievements: List<String>,
    val pressReleases: List<String>,
    val logoUrl: String = ""
)

@Entity(tableName = "candidates")
data class DbCandidate(
    @PrimaryKey val id: String,
    val partyId: String,
    val partyName: String,
    val name: String,
    val education: String,
    val profession: String,
    val assets: String,
    val liabilities: String,
    val declaredCriminalCases: Int,
    val electionHistory: String, // e.g. "2019: Won, 2014: Won"
    val attendance: String,      // e.g. "85%"
    val questionsAsked: Int,
    val billsIntroduced: Int,
    val constituencyName: String,
    val officialAffidavitUrl: String = "",
    val photoUrl: String = ""
)

@Entity(tableName = "constituencies")
data class DbConstituency(
    @PrimaryKey val id: String,
    val name: String,
    val state: String,
    val district: String,
    val pinCodes: String,
    val mpName: String,
    val mlaName: String,
    val population: String,
    val schoolsCount: Int,
    val hospitalsCount: Int,
    val roadsProgress: String,
    val waterProgress: String,
    val electricityProgress: String,
    val internetProgress: String,
    val budgetAllocation: String,
    val developmentProjects: List<String>
)

@Entity(tableName = "government_schemes")
data class DbGovernmentScheme(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val benefits: String,
    val eligibility: String,
    val category: String,
    val ministry: String,
    val sourceUrl: String
)

@Entity(tableName = "verified_news")
data class DbVerifiedNews(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val date: String,
    val source: String,
    val isFactCheck: Boolean,
    val factCheckVerdict: String = "", // e.g. "TRUE", "FALSE", "MISLEADING"
    val confidenceScore: Double = 1.0,
    val officialSources: List<String> = emptyList(),
    val originalUrl: String = ""
)

@Entity(tableName = "bookmarks")
data class DbBookmark(
    @PrimaryKey val id: String, // format: "type_itemId"
    val title: String,
    val type: String, // e.g., "party", "candidate", "scheme", "news", "constituency"
    val itemId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class DbSearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class DbChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionName: String, // to group chat sessions
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    // RAG Metadata
    val confidenceScore: Double? = null,
    val sourceCount: Int? = null,
    val lastUpdated: String? = null,
    val officialSources: List<String>? = null
)
