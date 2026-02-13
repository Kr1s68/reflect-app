package com.example.reflectapp.data.model

/**
 * Represents the mood associated with a journal entry.
 *
 * Each mood has a corresponding emoji for visual display and a human-readable label.
 * The ordinal value is stored in the Room database.
 */
enum class Mood(val emoji: String, val label: String) {
    /** User is feeling excellent. */
    GREAT("😄", "Great"),

    /** User is feeling positive. */
    GOOD("🙂", "Good"),

    /** User is feeling neutral. */
    OKAY("😐", "Okay"),

    /** User is feeling negative. */
    BAD("😔", "Bad"),

    /** User is feeling very negative. */
    TERRIBLE("😢", "Terrible")
}
