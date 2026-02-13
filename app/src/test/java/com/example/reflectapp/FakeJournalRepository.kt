package com.example.reflectapp

import com.example.reflectapp.data.repository.JournalRepository

/**
 * An in-memory [JournalRepository] for use in ViewModel unit tests.
 *
 * Wraps a [FakeJournalDao] so that all Flow-based queries and suspend functions
 * operate on an in-memory list, enabling fast, isolated tests without Room or coroutines overhead.
 */
class FakeJournalRepository : JournalRepository(FakeJournalDao())
