package com.example.reflectapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single journal entry.
 *
 * Stores all fields for a diary entry, including mood, category, optional photo, and timestamps.
 * The [mood] is stored as the ordinal of the [Mood] enum, and [category] as the name of the
 * [Category] enum.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    /** Auto-generated primary key. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Short title for the entry (max 100 characters). */
    val title: String,

    /** Full body text of the entry. */
    val content: String,

    /** Unix timestamp (ms) when the entry was created. */
    val dateCreated: Long = System.currentTimeMillis(),

    /** Unix timestamp (ms) when the entry was last modified. */
    val dateModified: Long = System.currentTimeMillis(),

    /** Ordinal of the [Mood] enum. Null if no mood was selected. */
    val mood: Int? = null,

    /** Name of the [Category] enum. Null if no category was selected. */
    val category: String? = null,

    /** URI string pointing to an attached photo, or null if none. */
    val photoUri: String? = null,

    /** Whether this entry has been marked as a favourite. */
    val isFavorite: Boolean = false
)
