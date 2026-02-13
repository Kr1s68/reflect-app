package com.example.reflectapp

import android.app.Application
import com.example.reflectapp.data.local.JournalDatabase
import com.example.reflectapp.data.repository.JournalRepository

/**
 * Custom [Application] class for the Reflect app.
 *
 * Acts as a manual dependency-injection container by providing lazy-initialised
 * instances of the database and repository that are shared across all Activities.
 */
class ReflectApplication : Application() {

    /** The Room database instance, created lazily on first access. */
    val database: JournalDatabase by lazy {
        JournalDatabase.getInstance(this)
    }

    /** The repository backed by the Room DAO, created lazily on first access. */
    val repository: JournalRepository by lazy {
        JournalRepository(database.journalDao())
    }
}
