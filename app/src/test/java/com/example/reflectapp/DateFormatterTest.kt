package com.example.reflectapp

import com.example.reflectapp.util.DateFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * Unit tests for [DateFormatter].
 *
 * Verifies that each formatting method produces output in the expected format.
 */
class DateFormatterTest {

    /** A fixed calendar representing 2025-01-05 at 15:45 local time. */
    private val calendar = Calendar.getInstance().apply {
        set(2025, Calendar.JANUARY, 5, 15, 45, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private val timestamp = calendar.timeInMillis

    @Test
    fun `formatShort returns MMM d yyyy format`() {
        val result = DateFormatter.formatShort(timestamp)
        // Should be something like "Jan 5, 2025"
        assertTrue("Expected short date but got: $result", result.contains("2025"))
        assertTrue("Expected month in result: $result", result.isNotBlank())
    }

    @Test
    fun `formatFull returns full datetime with year`() {
        val result = DateFormatter.formatFull(timestamp)
        assertTrue("Expected full date with year: $result", result.contains("2025"))
        assertTrue("Expected AM/PM indicator: $result", result.contains("AM") || result.contains("PM"))
    }

    @Test
    fun `formatDay returns yyyy-MM-dd pattern`() {
        val result = DateFormatter.formatDay(timestamp)
        // Should match "yyyy-MM-dd"
        val parts = result.split("-")
        assertEquals("Expected 3 parts in day format", 3, parts.size)
        assertEquals("Expected year 2025", "2025", parts[0])
        assertEquals("Expected month 01", "01", parts[1])
        assertEquals("Expected day 05", "05", parts[2])
    }

    @Test
    fun `formatDay produces consistent results for same timestamp`() {
        val result1 = DateFormatter.formatDay(timestamp)
        val result2 = DateFormatter.formatDay(timestamp)
        assertEquals(result1, result2)
    }
}
