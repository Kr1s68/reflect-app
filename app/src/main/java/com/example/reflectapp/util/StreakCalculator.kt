package com.example.reflectapp.util

import java.util.concurrent.TimeUnit

/**
 * Utility object for calculating the current writing streak from a list of entry timestamps.
 *
 * A "streak" is the number of consecutive calendar days (ending today or yesterday)
 * on which the user made at least one journal entry.
 */
object StreakCalculator {

    /**
     * Calculates the current writing streak from a list of creation timestamps.
     *
     * The algorithm:
     * 1. Converts all timestamps to unique calendar day strings ("yyyy-MM-dd").
     * 2. Sorts the days descending (newest first).
     * 3. Starting from today, counts consecutive days that have an entry.
     *    If today has no entry but yesterday does, starts counting from yesterday.
     * 4. Returns the count of consecutive days found.
     *
     * @param timestamps List of Unix timestamps (ms) from all journal entries.
     * @return The current streak in days. Returns 0 if the list is empty or the streak is broken.
     */
    fun calculate(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0

        // Deduplicate by calendar day
        val uniqueDays = timestamps
            .map { DateFormatter.formatDay(it) }
            .toSortedSet(reverseOrder())
            .toList()

        if (uniqueDays.isEmpty()) return 0

        val today = DateFormatter.formatDay(System.currentTimeMillis())
        val yesterday = DateFormatter.formatDay(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))

        // Streak must include today or yesterday to be active
        if (uniqueDays.first() != today && uniqueDays.first() != yesterday) return 0

        var streak = 0
        var expectedDay = uniqueDays.first()

        for (day in uniqueDays) {
            if (day == expectedDay) {
                streak++
                expectedDay = DateFormatter.formatDay(
                    parseDay(expectedDay) - TimeUnit.DAYS.toMillis(1)
                )
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Parses a "yyyy-MM-dd" string back to a Unix timestamp (ms, start of day UTC).
     */
    private fun parseDay(day: String): Long {
        val parts = day.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val dayOfMonth = parts[2].toInt()
        val cal = java.util.Calendar.getInstance()
        cal.set(year, month, dayOfMonth, 0, 0, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
