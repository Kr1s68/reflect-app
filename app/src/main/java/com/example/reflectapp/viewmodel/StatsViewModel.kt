package com.example.reflectapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reflectapp.data.model.CategoryCount
import com.example.reflectapp.data.model.MoodCount
import com.example.reflectapp.data.repository.JournalRepository
import com.example.reflectapp.util.StreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for [com.example.reflectapp.ui.settings.SettingsActivity].
 *
 * Aggregates statistics from the repository: total entry count, writing streak,
 * mood distribution, and category breakdown. Call [loadStats] when the screen
 * is visible to trigger a fresh fetch.
 *
 * @property repository The data source for journal entries.
 */
class StatsViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _totalEntries = MutableStateFlow(0)
    /** Total number of journal entries stored in the database. */
    val totalEntries: StateFlow<Int> = _totalEntries.asStateFlow()

    private val _streak = MutableStateFlow(0)
    /** Current consecutive-day writing streak. */
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _moodCounts = MutableStateFlow<List<MoodCount>>(emptyList())
    /** Count of entries per mood ordinal, for displaying a mood distribution chart. */
    val moodCounts: StateFlow<List<MoodCount>> = _moodCounts.asStateFlow()

    private val _categoryCounts = MutableStateFlow<List<CategoryCount>>(emptyList())
    /** Count of entries per category name, for displaying a category breakdown. */
    val categoryCounts: StateFlow<List<CategoryCount>> = _categoryCounts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    /** True while statistics are being fetched from the database. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fetches all statistics from the repository and updates the exposed StateFlows.
     * Should be called from the UI when the statistics screen becomes visible.
     */
    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _totalEntries.value = repository.getTotalCount()
            _moodCounts.value = repository.getMoodCounts()
            _categoryCounts.value = repository.getCategoryCounts()
            val dates = repository.getAllCreatedDates()
            _streak.value = StreakCalculator.calculate(dates)
            _isLoading.value = false
        }
    }
}
