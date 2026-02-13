package com.example.reflectapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for formatting Unix timestamps into human-readable strings.
 */
object DateFormatter {

    private val fullFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Formats a timestamp as a full date-time string, e.g. "January 5, 2025 at 3:45 PM".
     *
     * @param timestamp Unix timestamp in milliseconds.
     */
    fun formatFull(timestamp: Long): String = fullFormat.format(Date(timestamp))

    /**
     * Formats a timestamp as a short date string, e.g. "Jan 5, 2025".
     *
     * @param timestamp Unix timestamp in milliseconds.
     */
    fun formatShort(timestamp: Long): String = shortFormat.format(Date(timestamp))

    /**
     * Formats a timestamp as a day string in "yyyy-MM-dd" format.
     * Used by [com.example.reflectapp.util.StreakCalculator] for day comparison.
     *
     * @param timestamp Unix timestamp in milliseconds.
     */
    fun formatDay(timestamp: Long): String = dayFormat.format(Date(timestamp))
}
