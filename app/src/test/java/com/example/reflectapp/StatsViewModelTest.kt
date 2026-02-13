package com.example.reflectapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.viewmodel.StatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [StatsViewModel].
 *
 * Verifies that statistics aggregation (total count, mood counts, category counts)
 * is correct after loading.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeJournalRepository
    private lateinit var viewModel: StatsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeJournalRepository()
        viewModel = StatsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun insertEntry(
        title: String = "Entry",
        mood: Mood? = null,
        category: Category? = null,
        daysAgo: Int = 0
    ): Long {
        val ts = System.currentTimeMillis() - daysAgo * 86_400_000L
        return repository.insert(
            JournalEntry(
                title = title,
                content = "Content",
                dateCreated = ts,
                mood = mood?.ordinal,
                category = category?.name
            )
        )
    }

    @Test
    fun `loadStats returns zero total for empty database`() = runTest {
        viewModel.loadStats()
        advanceUntilIdle()
        assertEquals(0, viewModel.totalEntries.value)
    }

    @Test
    fun `loadStats returns correct total entry count`() = runTest {
        insertEntry()
        insertEntry()
        insertEntry()
        viewModel.loadStats()
        advanceUntilIdle()
        assertEquals(3, viewModel.totalEntries.value)
    }

    @Test
    fun `loadStats aggregates mood counts correctly`() = runTest {
        insertEntry(mood = Mood.GREAT)
        insertEntry(mood = Mood.GREAT)
        insertEntry(mood = Mood.BAD)
        viewModel.loadStats()
        advanceUntilIdle()
        val moodCounts = viewModel.moodCounts.value
        val greatCount = moodCounts.find { it.mood == Mood.GREAT.ordinal }?.count ?: 0
        val badCount = moodCounts.find { it.mood == Mood.BAD.ordinal }?.count ?: 0
        assertEquals(2, greatCount)
        assertEquals(1, badCount)
    }

    @Test
    fun `loadStats aggregates category counts correctly`() = runTest {
        insertEntry(category = Category.WORK)
        insertEntry(category = Category.WORK)
        insertEntry(category = Category.TRAVEL)
        viewModel.loadStats()
        advanceUntilIdle()
        val categoryCounts = viewModel.categoryCounts.value
        val workCount = categoryCounts.find { it.category == Category.WORK.name }?.count ?: 0
        val travelCount = categoryCounts.find { it.category == Category.TRAVEL.name }?.count ?: 0
        assertEquals(2, workCount)
        assertEquals(1, travelCount)
    }

    @Test
    fun `loadStats calculates streak for consecutive days`() = runTest {
        // Entries on today, yesterday, and 2 days ago
        insertEntry(daysAgo = 0)
        insertEntry(daysAgo = 1)
        insertEntry(daysAgo = 2)
        viewModel.loadStats()
        advanceUntilIdle()
        assertTrue("Streak should be at least 3", viewModel.streak.value >= 3)
    }

    @Test
    fun `isLoading starts false and returns to false after load`() = runTest {
        assertEquals(false, viewModel.isLoading.value)
        viewModel.loadStats()
        advanceUntilIdle()
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `entries with no mood are not counted in mood stats`() = runTest {
        insertEntry(mood = null)
        insertEntry(mood = null)
        viewModel.loadStats()
        advanceUntilIdle()
        assertTrue("Mood counts should be empty when no moods set", viewModel.moodCounts.value.isEmpty())
    }
}
