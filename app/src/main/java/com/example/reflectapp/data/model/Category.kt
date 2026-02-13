package com.example.reflectapp.data.model

/**
 * Represents the category/tag assigned to a journal entry.
 *
 * Each category has a display name and an associated color resource value
 * (as a hex string) for visual distinction in the UI.
 */
enum class Category(val displayName: String, val colorHex: String) {
    /** Personal thoughts and reflections. */
    PERSONAL("Personal", "#7C4DFF"),

    /** Work-related entries. */
    WORK("Work", "#448AFF"),

    /** Travel experiences and adventures. */
    TRAVEL("Travel", "#00BCD4"),

    /** Health and wellness tracking. */
    HEALTH("Health", "#4CAF50"),

    /** Things to be grateful for. */
    GRATITUDE("Gratitude", "#FF9800"),

    /** New ideas and brainstorming. */
    IDEAS("Ideas", "#E91E63"),

    /** Miscellaneous entries that don't fit elsewhere. */
    OTHER("Other", "#9E9E9E")
}
