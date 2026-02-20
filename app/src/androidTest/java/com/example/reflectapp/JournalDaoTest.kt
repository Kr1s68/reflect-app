package com.example.reflectapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reflectapp.data.local.JournalDatabase
import com.example.reflectapp.data.local.JournalDao
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented tests for [JournalDao].
 *
 * Runs against a real in-memory Room database on the Android device/emulator.
 * Each test starts with a clean database.
 */
@RunWith(AndroidJUnit4::class)
class JournalDaoTest {

    private lateinit var db: JournalDatabase
    private lateinit var dao: JournalDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, JournalDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.journalDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun makeEntry(
        title: String = "Title",
        content: String = "Content",
        mood: Mood? = null,
        category: Category? = null,
        isFavorite: Boolean = false
    ) = JournalEntry(
        title = title,
        content = content,
        mood = mood?.ordinal,
        category = category?.name,
        isFavorite = isFavorite
    )

    // ---- Insert ----

    @Test
    fun insertAndGetById() = runTest {
        val id = dao.insert(makeEntry(title = "My entry"))
        val entry = dao.getEntryById(id).first()
        assertNotNull(entry)
        assertEquals("My entry", entry?.title)
    }

    @Test
    fun insertReturnsPositiveId() = runTest {
        val id = dao.insert(makeEntry())
        assertTrue("Inserted ID should be > 0", id > 0)
    }

    // ---- Update ----

    @Test
    fun updateChangesTitle() = runTest {
        val id = dao.insert(makeEntry(title = "Original"))
        val original = dao.getEntryById(id).first()!!
        dao.update(original.copy(title = "Updated"))
        val updated = dao.getEntryById(id).first()
        assertEquals("Updated", updated?.title)
    }

    // ---- Delete ----

    @Test
    fun deleteRemovesEntry() = runTest {
        val id = dao.insert(makeEntry())
        val entry = dao.getEntryById(id).first()!!
        dao.delete(entry)
        val result = dao.getEntryById(id).first()
        assertNull(result)
    }

    // ---- GetAll ----

    @Test
    fun getAllEntriesReturnsAll() = runTest {
        dao.insert(makeEntry(title = "One"))
        dao.insert(makeEntry(title = "Two"))
        dao.insert(makeEntry(title = "Three"))
        val all = dao.getAllEntries().first()
        assertEquals(3, all.size)
    }

    @Test
    fun getAllEntriesIsOrderedByDateDescending() = runTest {
        val now = System.currentTimeMillis()
        dao.insert(makeEntry(title = "Old").copy(dateCreated = now - 10_000))
        dao.insert(makeEntry(title = "New").copy(dateCreated = now))
        val all = dao.getAllEntries().first()
        assertEquals("New", all[0].title)
        assertEquals("Old", all[1].title)
    }

    // ---- Search ----

    @Test
    fun searchEntriesFindsMatchInTitle() = runTest {
        dao.insert(makeEntry(title = "Morning Jog"))
        dao.insert(makeEntry(title = "Evening Walk"))
        val results = dao.searchEntries("morning").first()
        assertEquals(1, results.size)
        assertEquals("Morning Jog", results[0].title)
    }

    @Test
    fun searchEntriesFindsMatchInContent() = runTest {
        dao.insert(makeEntry(content = "Went hiking in the mountains"))
        dao.insert(makeEntry(content = "Stayed home and read"))
        val results = dao.searchEntries("hiking").first()
        assertEquals(1, results.size)
    }

    @Test
    fun searchEntriesIsCaseInsensitive() = runTest {
        dao.insert(makeEntry(title = "HELLO WORLD"))
        val results = dao.searchEntries("hello").first()
        assertEquals(1, results.size)
    }

    // ---- Filter by mood ----

    @Test
    fun getEntriesByMoodFiltersCorrectly() = runTest {
        dao.insert(makeEntry(mood = Mood.HAPPY))
        dao.insert(makeEntry(mood = Mood.GRATEFUL))
        dao.insert(makeEntry(mood = Mood.OVERWHELMED))
        dao.insert(makeEntry(mood = Mood.SAD))
        dao.insert(makeEntry(mood = Mood.ANGRY))
        dao.insert(makeEntry(mood = Mood.ANXIOUS))
        dao.insert(makeEntry(mood = Mood.CALM))
        dao.insert(makeEntry(mood = Mood.CONTENT))
        dao.insert(makeEntry(mood = Mood.EXCITED))
        dao.insert(makeEntry(mood = Mood.HOPEFUL))
        dao.insert(makeEntry(mood = Mood.LONELY))
        dao.insert(makeEntry(mood = Mood.TIRED))
        val results = dao.getEntriesByMood(Mood.HAPPY.ordinal).first()
        assertEquals(1, results.size)
        assertTrue(results.all { it.mood == Mood.HAPPY.ordinal })
    }

    // ---- Filter by category ----

    @Test
    fun getEntriesByCategoryFiltersCorrectly() = runTest {
        dao.insert(makeEntry(category = Category.WORK))
        dao.insert(makeEntry(category = Category.TRAVEL))
        dao.insert(makeEntry(category = Category.WORK))
        val results = dao.getEntriesByCategory(Category.WORK.name).first()
        assertEquals(2, results.size)
        assertTrue(results.all { it.category == Category.WORK.name })
    }

    // ---- Favourites ----

    @Test
    fun getFavoriteEntriesReturnsOnlyFavorites() = runTest {
        dao.insert(makeEntry(isFavorite = true))
        dao.insert(makeEntry(isFavorite = false))
        dao.insert(makeEntry(isFavorite = true))
        val favorites = dao.getFavoriteEntries().first()
        assertEquals(2, favorites.size)
        assertTrue(favorites.all { it.isFavorite })
    }

    // ---- Statistics ----

    @Test
    fun getTotalCountReturnsCorrectNumber() = runTest {
        dao.insert(makeEntry())
        dao.insert(makeEntry())
        assertEquals(2, dao.getTotalCount())
    }

    @Test
    fun getMoodCountsAggregatesCorrectly() = runTest {
        dao.insert(makeEntry(mood = Mood.HAPPY))
        dao.insert(makeEntry(mood = Mood.HAPPY))
        dao.insert(makeEntry(mood = Mood.GRATEFUL))
        dao.insert(makeEntry(mood = Mood.OVERWHELMED))
        dao.insert(makeEntry(mood = Mood.SAD))
        dao.insert(makeEntry(mood = Mood.ANGRY))
        dao.insert(makeEntry(mood = Mood.ANXIOUS))
        dao.insert(makeEntry(mood = Mood.CALM))
        dao.insert(makeEntry(mood = Mood.CONTENT))
        dao.insert(makeEntry(mood = Mood.EXCITED))
        dao.insert(makeEntry(mood = Mood.HOPEFUL))
        dao.insert(makeEntry(mood = Mood.LONELY))
        dao.insert(makeEntry(mood = Mood.TIRED))
        val counts = dao.getMoodCounts()
        val greatCount = counts.find { it.mood == Mood.HAPPY.ordinal }?.count ?: 0
        assertEquals(2, greatCount)
    }

    @Test
    fun getCategoryCountsAggregatesCorrectly() = runTest {
        dao.insert(makeEntry(category = Category.HEALTH))
        dao.insert(makeEntry(category = Category.HEALTH))
        dao.insert(makeEntry(category = Category.PERSONAL))
        val counts = dao.getCategoryCounts()
        val healthCount = counts.find { it.category == Category.HEALTH.name }?.count ?: 0
        assertEquals(2, healthCount)
    }

    @Test
    fun getAllCreatedDatesReturnsAllTimestamps() = runTest {
        dao.insert(makeEntry())
        dao.insert(makeEntry())
        val dates = dao.getAllCreatedDates()
        assertEquals(2, dates.size)
    }
}
