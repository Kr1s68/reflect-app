package com.example.reflectapp.data.model

/**
 * DAO query helper that maps a mood ordinal to its entry count.
 *
 * Used by [com.example.reflectapp.data.local.JournalDao.getMoodCounts] to return
 * aggregated mood statistics.
 */
data class MoodCount(
    /** Ordinal of the [Mood] enum. */
    val mood: Int,

    /** Number of entries with this mood. */
    val count: Int
)
