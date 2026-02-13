package com.example.reflectapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.viewmodel.JournalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [JournalViewModel].
 *
 * Uses a [FakeJournalRepository] backed by an in-memory [FakeJournalDao]
 * so that no Android framework components are required.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeJournalRepository
    private lateinit var viewModel: JournalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeJournalRepository()
        viewModel = JournalViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeEntry(
        title: String = "Test",
        content: String = "Content",
        mood: Mood? = null,
        category: Category? = null
    ) = JournalEntry(title = title, content = content, mood = mood?.ordinal, category = category?.name)

    // ---- Insert / observe ----

    @Test
    fun `entries emits inserted entry`() = runTest {
        repository.insert(makeEntry(title = "Hello"))
        advanceUntilIdle()
        val entries = viewModel.entries.first()
        assertEquals(1, entries.size)
        assertEquals("Hello", entries[0].title)
    }

    @Test
    fun `entries emits all inserted entries`() = runTest {
        repository.insert(makeEntry(title = "One"))
        repository.insert(makeEntry(title = "Two"))
        repository.insert(makeEntry(title = "Three"))
        advanceUntilIdle()
        val entries = viewModel.entries.first()
        assertEquals(3, entries.size)
    }

    // ---- Delete ----

    @Test
    fun `deleteEntry removes entry from list`() = runTest {
        val id = repository.insert(makeEntry(title = "To Delete"))
        advanceUntilIdle()
        val entry = viewModel.entries.first().first { it.id == id }
        viewModel.deleteEntry(entry)
        advanceUntilIdle()
        val remaining = viewModel.entries.first()
        assertTrue("Entry should be deleted", remaining.none { it.id == id })
    }

    // ---- Toggle favourite ----

    @Test
    fun `toggleFavorite flips isFavorite to true`() = runTest {
        val id = repository.insert(makeEntry())
        advanceUntilIdle()
        val entry = viewModel.entries.first().first { it.id == id }
        assertFalse(entry.isFavorite)
        viewModel.toggleFavorite(entry)
        advanceUntilIdle()
        val updated = viewModel.entries.first().first { it.id == id }
        assertTrue(updated.isFavorite)
    }

    @Test
    fun `toggleFavorite flips isFavorite back to false`() = runTest {
        val id = repository.insert(makeEntry().copy(isFavorite = true))
        advanceUntilIdle()
        val entry = viewModel.entries.first().first { it.id == id }
        viewModel.toggleFavorite(entry)
        advanceUntilIdle()
        val updated = viewModel.entries.first().first { it.id == id }
        assertFalse(updated.isFavorite)
    }

    // ---- Search ----

    @Test
    fun `search query filters entries by title`() = runTest {
        repository.insert(makeEntry(title = "Morning Thoughts"))
        repository.insert(makeEntry(title = "Evening Walk"))
        advanceUntilIdle()
        viewModel.onSearchQueryChanged("morning")
        advanceUntilIdle()
        val results = viewModel.entries.first()
        assertEquals(1, results.size)
        assertEquals("Morning Thoughts", results[0].title)
    }

    @Test
    fun `search query filters entries by content`() = runTest {
        repository.insert(makeEntry(title = "Day 1", content = "Went hiking today"))
        repository.insert(makeEntry(title = "Day 2", content = "Stayed home"))
        advanceUntilIdle()
        viewModel.onSearchQueryChanged("hiking")
        advanceUntilIdle()
        val results = viewModel.entries.first()
        assertEquals(1, results.size)
    }

    @Test
    fun `clear filters resets search query and mood and category`() = runTest {
        viewModel.onSearchQueryChanged("test")
        viewModel.onMoodFilterChanged(Mood.GOOD)
        viewModel.clearFilters()
        assertEquals("", viewModel.searchQuery.value)
        assertEquals(null, viewModel.selectedMood.value)
        assertEquals(null, viewModel.selectedCategory.value)
    }

    // ---- Mood filter ----

    @Test
    fun `mood filter returns only matching entries`() = runTest {
        repository.insert(makeEntry(mood = Mood.GREAT))
        repository.insert(makeEntry(mood = Mood.BAD))
        advanceUntilIdle()
        viewModel.onMoodFilterChanged(Mood.GREAT)
        advanceUntilIdle()
        val results = viewModel.entries.first()
        assertEquals(1, results.size)
        assertEquals(Mood.GREAT.ordinal, results[0].mood)
    }

    // ---- Category filter ----

    @Test
    fun `category filter returns only matching entries`() = runTest {
        repository.insert(makeEntry(category = Category.WORK))
        repository.insert(makeEntry(category = Category.TRAVEL))
        advanceUntilIdle()
        viewModel.onCategoryFilterChanged(Category.WORK)
        advanceUntilIdle()
        val results = viewModel.entries.first()
        assertEquals(1, results.size)
        assertEquals(Category.WORK.name, results[0].category)
    }

    @Test
    fun `setting mood filter clears category filter`() = runTest {
        viewModel.onCategoryFilterChanged(Category.HEALTH)
        viewModel.onMoodFilterChanged(Mood.OKAY)
        assertEquals(null, viewModel.selectedCategory.value)
        assertEquals(Mood.OKAY, viewModel.selectedMood.value)
    }
}
