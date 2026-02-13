package com.example.reflectapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reflectapp.data.model.CategoryCount
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.MoodCount
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [JournalEntry] operations.
 *
 * All queries returning lists use [Flow] so that the UI automatically updates
 * when the underlying data changes.
 */
@Dao
interface JournalDao {

    // ---- Write operations ----

    /**
     * Inserts a new journal entry. Returns the new row ID.
     * If a conflicting entry exists, it is replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry): Long

    /** Updates an existing journal entry. */
    @Update
    suspend fun update(entry: JournalEntry)

    /** Deletes a journal entry. */
    @Delete
    suspend fun delete(entry: JournalEntry)

    // ---- Read operations ----

    /**
     * Returns all journal entries, ordered by creation date descending (newest first).
     * Emits a new list whenever the table changes.
     */
    @Query("SELECT * FROM journal_entries ORDER BY dateCreated DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    /**
     * Returns a single journal entry by its [id], or null if not found.
     * Emits updates whenever the entry changes.
     */
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<JournalEntry?>

    /**
     * Returns entries whose title or content contains [query] (case-insensitive),
     * ordered by creation date descending.
     */
    @Query(
        """
        SELECT * FROM journal_entries
        WHERE title LIKE '%' || :query || '%'
           OR content LIKE '%' || :query || '%'
        ORDER BY dateCreated DESC
        """
    )
    fun searchEntries(query: String): Flow<List<JournalEntry>>

    /**
     * Returns entries filtered by a specific [mood] ordinal,
     * ordered by creation date descending.
     */
    @Query("SELECT * FROM journal_entries WHERE mood = :mood ORDER BY dateCreated DESC")
    fun getEntriesByMood(mood: Int): Flow<List<JournalEntry>>

    /**
     * Returns entries filtered by a specific [category] name,
     * ordered by creation date descending.
     */
    @Query("SELECT * FROM journal_entries WHERE category = :category ORDER BY dateCreated DESC")
    fun getEntriesByCategory(category: String): Flow<List<JournalEntry>>

    /**
     * Returns only entries marked as favourite, ordered by creation date descending.
     */
    @Query("SELECT * FROM journal_entries WHERE isFavorite = 1 ORDER BY dateCreated DESC")
    fun getFavoriteEntries(): Flow<List<JournalEntry>>

    // ---- Statistics queries ----

    /**
     * Returns the total number of journal entries.
     */
    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getTotalCount(): Int

    /**
     * Returns the count of entries per mood ordinal, excluding entries with no mood.
     */
    @Query(
        """
        SELECT mood, COUNT(*) as count
        FROM journal_entries
        WHERE mood IS NOT NULL
        GROUP BY mood
        """
    )
    suspend fun getMoodCounts(): List<MoodCount>

    /**
     * Returns the count of entries per category name, excluding entries with no category.
     */
    @Query(
        """
        SELECT category, COUNT(*) as count
        FROM journal_entries
        WHERE category IS NOT NULL
        GROUP BY category
        """
    )
    suspend fun getCategoryCounts(): List<CategoryCount>

    /**
     * Returns a list of distinct dates (epoch ms) on which entries were created,
     * ordered ascending. Used for streak calculation.
     */
    @Query("SELECT DISTINCT dateCreated FROM journal_entries ORDER BY dateCreated ASC")
    suspend fun getAllCreatedDates(): List<Long>
}
