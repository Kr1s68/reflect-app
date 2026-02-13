package com.example.reflectapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.data.repository.JournalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for [com.example.reflectapp.ui.home.MainActivity].
 *
 * Manages the list of journal entries and exposes reactive state for the home screen.
 * Handles search, mood filter, category filter, and favourite toggling.
 *
 * @property repository The data source for journal entries.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    /** Current text entered in the search bar. Empty string means no search. */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    /** Currently selected mood filter, or null if no mood filter is active. */
    private val _selectedMood = MutableStateFlow<Mood?>(null)
    val selectedMood: StateFlow<Mood?> = _selectedMood

    /** Currently selected category filter, or null if no category filter is active. */
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    /**
     * The filtered list of journal entries exposed to the UI.
     *
     * Combines search query, mood filter, and category filter to determine which
     * Room query to execute, then applies any remaining in-memory filters.
     */
    val entries: StateFlow<List<JournalEntry>> = combine(
        _searchQuery,
        _selectedMood,
        _selectedCategory
    ) { query, mood, category ->
        Triple(query, mood, category)
    }.flatMapLatest { (query, mood, category) ->
        when {
            query.isNotBlank() -> repository.searchEntries(query)
            mood != null -> repository.getEntriesByMood(mood.ordinal)
            category != null -> repository.getEntriesByCategory(category.name)
            else -> repository.allEntries
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // ---- User actions ----

    /**
     * Updates the search query. Triggers a new database search.
     *
     * @param query The search string entered by the user.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /**
     * Sets the active mood filter. Pass null to clear the filter.
     *
     * @param mood The [Mood] to filter by, or null to show all moods.
     */
    fun onMoodFilterChanged(mood: Mood?) {
        _selectedMood.value = mood
        if (mood != null) _selectedCategory.value = null
    }

    /**
     * Sets the active category filter. Pass null to clear the filter.
     *
     * @param category The [Category] to filter by, or null to show all categories.
     */
    fun onCategoryFilterChanged(category: Category?) {
        _selectedCategory.value = category
        if (category != null) _selectedMood.value = null
    }

    /** Clears all active search and filter state. */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedMood.value = null
        _selectedCategory.value = null
    }

    /**
     * Toggles the favourite status of the given [entry].
     *
     * @param entry The entry whose favourite flag should be toggled.
     */
    fun toggleFavorite(entry: JournalEntry) {
        viewModelScope.launch {
            repository.update(entry.copy(isFavorite = !entry.isFavorite))
        }
    }

    /**
     * Deletes the given [entry] from the database.
     *
     * @param entry The entry to delete.
     */
    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}
