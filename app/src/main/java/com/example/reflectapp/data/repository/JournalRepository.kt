package com.example.reflectapp.data.repository

import com.example.reflectapp.data.local.JournalDao
import com.example.reflectapp.data.model.CategoryCount
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.MoodCount
import kotlinx.coroutines.flow.Flow

/**
 * Repository that abstracts access to the [JournalDao].
 *
 * ViewModels should interact with data exclusively through this class.
 * This makes it easy to swap the data source (e.g., replace the DAO with a fake in tests).
 *
 * @property dao The Room DAO used to perform database operations.
 */
open class JournalRepository(private val dao: JournalDao) {

    // ---- Flows (observed by ViewModels) ----

    /** Flow of all journal entries, newest first. */
    val allEntries: Flow<List<JournalEntry>> = dao.getAllEntries()

    /** Flow of favourite journal entries, newest first. */
    val favoriteEntries: Flow<List<JournalEntry>> = dao.getFavoriteEntries()

    /**
     * Returns a [Flow] that emits the entry with the given [id], or null if not found.
     */
    fun getEntryById(id: Long): Flow<JournalEntry?> = dao.getEntryById(id)

    /**
     * Returns a [Flow] of entries whose title or content matches [query].
     */
    fun searchEntries(query: String): Flow<List<JournalEntry>> = dao.searchEntries(query)

    /**
     * Returns a [Flow] of entries filtered to a specific [mood] ordinal.
     */
    fun getEntriesByMood(mood: Int): Flow<List<JournalEntry>> = dao.getEntriesByMood(mood)

    /**
     * Returns a [Flow] of entries filtered to a specific [category] name.
     */
    fun getEntriesByCategory(category: String): Flow<List<JournalEntry>> =
        dao.getEntriesByCategory(category)

    // ---- Suspend functions (one-shot operations) ----

    /**
     * Inserts a new [entry] and returns its generated row ID.
     */
    suspend fun insert(entry: JournalEntry): Long = dao.insert(entry)

    /**
     * Updates an existing [entry].
     */
    suspend fun update(entry: JournalEntry) = dao.update(entry)

    /**
     * Deletes the given [entry].
     */
    suspend fun delete(entry: JournalEntry) = dao.delete(entry)

    /**
     * Returns the total number of journal entries.
     */
    suspend fun getTotalCount(): Int = dao.getTotalCount()

    /**
     * Returns a list of [MoodCount] objects (mood ordinal + count) for statistics.
     */
    suspend fun getMoodCounts(): List<MoodCount> = dao.getMoodCounts()

    /**
     * Returns a list of [CategoryCount] objects (category name + count) for statistics.
     */
    suspend fun getCategoryCounts(): List<CategoryCount> = dao.getCategoryCounts()

    /**
     * Returns all distinct creation timestamps, ordered ascending.
     * Used by [com.example.reflectapp.util.StreakCalculator].
     */
    suspend fun getAllCreatedDates(): List<Long> = dao.getAllCreatedDates()
}
