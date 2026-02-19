package com.example.reflectapp.data.model

/**
 * Represents the emotional state associated with a journal entry.
 *
 * Each mood has a corresponding emoji for visual display and a human-readable label.
 * The ordinal value is stored in the Room database.
 */
enum class Mood(val emoji: String, val label: String) {
    /** User is feeling joyful and happy. */
    HAPPY("😊", "Happy"),

    /** User is feeling down or sad. */
    SAD("😢", "Sad"),

    /** User is feeling worried or anxious. */
    ANXIOUS("😰", "Anxious"),

    /** User is feeling enthusiastic or excited. */
    EXCITED("🤩", "Excited"),

    /** User is feeling peaceful and calm. */
    CALM("😌", "Calm"),

    /** User is feeling angry or frustrated. */
    ANGRY("😠", "Angry"),

    /** User is feeling lonely or missing someone. */
    LONELY("💔", "Lonely"),

    /** User is feeling thankful and grateful. */
    GRATEFUL("🙏", "Grateful"),

    /** User is feeling overwhelmed or stressed. */
    OVERWHELMED("😵", "Overwhelmed"),

    /** User is feeling hopeful and optimistic. */
    HOPEFUL("🌟", "Hopeful"),

    /** User is feeling satisfied and content. */
    CONTENT("😇", "Content"),

    /** User is feeling tired or exhausted. */
    TIRED("😴", "Tired")
}
