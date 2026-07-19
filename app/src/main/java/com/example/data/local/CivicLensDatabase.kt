package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DbPoliticalParty::class,
        DbCandidate::class,
        DbConstituency::class,
        DbGovernmentScheme::class,
        DbVerifiedNews::class,
        DbBookmark::class,
        DbSearchHistory::class,
        DbChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CivicLensDatabase : RoomDatabase() {
    abstract fun civicLensDao(): CivicLensDao
}
