package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CivicLensDao {

    // --- Political Parties ---
    @Query("SELECT * FROM political_parties")
    fun getAllParties(): Flow<List<DbPoliticalParty>>

    @Query("SELECT * FROM political_parties WHERE id = :id")
    fun getPartyById(id: String): Flow<DbPoliticalParty?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParties(parties: List<DbPoliticalParty>)

    // --- Candidates ---
    @Query("SELECT * FROM candidates")
    fun getAllCandidates(): Flow<List<DbCandidate>>

    @Query("SELECT * FROM candidates WHERE partyId = :partyId")
    fun getCandidatesByParty(partyId: String): Flow<List<DbCandidate>>

    @Query("SELECT * FROM candidates WHERE id = :id")
    fun getCandidateById(id: String): Flow<DbCandidate?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidates(candidates: List<DbCandidate>)

    // --- Constituencies ---
    @Query("SELECT * FROM constituencies")
    fun getAllConstituencies(): Flow<List<DbConstituency>>

    @Query("SELECT * FROM constituencies WHERE id = :id")
    fun getConstituencyById(id: String): Flow<DbConstituency?>

    @Query("SELECT * FROM constituencies WHERE pinCodes LIKE '%' || :pinCode || '%'")
    suspend fun searchConstituencyByPinCode(pinCode: String): DbConstituency?

    @Query("SELECT * FROM constituencies WHERE name LIKE '%' || :query || '%' OR mpName LIKE '%' || :query || '%'")
    fun searchConstituencies(query: String): Flow<List<DbConstituency>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstituencies(constituencies: List<DbConstituency>)

    // --- Schemes ---
    @Query("SELECT * FROM government_schemes")
    fun getAllSchemes(): Flow<List<DbGovernmentScheme>>

    @Query("SELECT * FROM government_schemes WHERE id = :id")
    fun getSchemeById(id: String): Flow<DbGovernmentScheme?>

    @Query("SELECT * FROM government_schemes WHERE category = :category")
    fun getSchemesByCategory(category: String): Flow<List<DbGovernmentScheme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchemes(schemes: List<DbGovernmentScheme>)

    // --- Verified News / Fact Checks ---
    @Query("SELECT * FROM verified_news ORDER BY date DESC")
    fun getAllNews(): Flow<List<DbVerifiedNews>>

    @Query("SELECT * FROM verified_news WHERE isFactCheck = :isFactCheck ORDER BY date DESC")
    fun getNewsFiltered(isFactCheck: Boolean): Flow<List<DbVerifiedNews>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(newsList: List<DbVerifiedNews>)

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<DbBookmark>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: DbBookmark)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // --- Search History ---
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getSearchHistory(): Flow<List<DbSearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(search: DbSearchHistory)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE sessionName = :sessionName ORDER BY timestamp ASC")
    fun getChatMessages(sessionName: String): Flow<List<DbChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: DbChatMessage)

    @Query("DELETE FROM chat_messages WHERE sessionName = :sessionName")
    suspend fun clearChatSession(sessionName: String)
}
