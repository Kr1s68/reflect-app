package com.example.reflectapp

import com.example.reflectapp.data.local.JournalDao
import com.example.reflectapp.data.model.CategoryCount
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.MoodCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory fake implementation of [JournalDao] for use in unit tests.
 *
 * Uses [MutableStateFlow] so that Flow-based queries automatically emit updates
 * when the backing list changes, mirroring real Room behaviour.
 */
class FakeJournalDao : JournalDao {

    private val _entries = MutableStateFlow<List<JournalEntry>>(emptyList())
    private var nextId = 1L

    override suspend fun insert(entry: JournalEntry): Long {
        val id = nextId++
        val stored = entry.copy(id = id)
        _entries.value = _entries.value + stored
        return id
    }

    override suspend fun update(entry: JournalEntry) {
        _entries.value = _entries.value.map { if (it.id == entry.id) entry else it }
    }

    override suspend fun delete(entry: JournalEntry) {
        _entries.value = _entries.value.filter { it.id != entry.id }
    }

    override fun getAllEntries(): Flow<List<JournalEntry>> =
        _entries.map { it.sortedByDescending { e -> e.dateCreated } }

    override fun getEntryById(id: Long): Flow<JournalEntry?> =
        _entries.map { list -> list.find { it.id == id } }

    override fun searchEntries(query: String): Flow<List<JournalEntry>> =
        _entries.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
            }.sortedByDescending { it.dateCreated }
        }

    override fun getEntriesByMood(mood: Int): Flow<List<JournalEntry>> =
        _entries.map { list ->
            list.filter { it.mood == mood }.sortedByDescending { it.dateCreated }
        }

    override fun getEntriesByCategory(category: String): Flow<List<JournalEntry>> =
        _entries.map { list ->
            list.filter { it.category == category }.sortedByDescending { it.dateCreated }
        }

    override fun getFavoriteEntries(): Flow<List<JournalEntry>> =
        _entries.map { list ->
            list.filter { it.isFavorite }.sortedByDescending { it.dateCreated }
        }

    override suspend fun getTotalCount(): Int = _entries.value.size

    override suspend fun getMoodCounts(): List<MoodCount> =
        _entries.value
            .filter { it.mood != null }
            .groupBy { it.mood!! }
            .map { (mood, entries) -> MoodCount(mood, entries.size) }

    override suspend fun getCategoryCounts(): List<CategoryCount> =
        _entries.value
            .filter { it.category != null }
            .groupBy { it.category!! }
            .map { (cat, entries) -> CategoryCount(cat, entries.size) }

    override suspend fun getAllCreatedDates(): List<Long> =
        _entries.value.map { it.dateCreated }.sorted()
}
