package com.example.reflectapp

import com.example.reflectapp.util.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Unit tests for [StreakCalculator].
 *
 * Tests edge cases including empty input, consecutive days, gaps in writing,
 * and streaks that do not include today.
 */
class StreakCalculatorTest {

    private fun timestampForDaysAgo(daysAgo: Int): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    @Test
    fun `empty list returns zero`() {
        assertEquals(0, StreakCalculator.calculate(emptyList()))
    }

    @Test
    fun `single entry today returns streak of 1`() {
        val timestamps = listOf(timestampForDaysAgo(0))
        assertEquals(1, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `single entry yesterday returns streak of 1`() {
        val timestamps = listOf(timestampForDaysAgo(1))
        assertEquals(1, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `entry 2 days ago with no today or yesterday returns 0`() {
        val timestamps = listOf(timestampForDaysAgo(2))
        assertEquals(0, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `consecutive days today and yesterday returns streak of 2`() {
        val timestamps = listOf(
            timestampForDaysAgo(0),
            timestampForDaysAgo(1)
        )
        assertEquals(2, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `five consecutive days returns streak of 5`() {
        val timestamps = (0..4).map { timestampForDaysAgo(it) }
        assertEquals(5, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `gap breaks streak - today and 2 days ago returns 1`() {
        val timestamps = listOf(
            timestampForDaysAgo(0),
            timestampForDaysAgo(2) // gap on day 1
        )
        assertEquals(1, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `multiple entries same day count as one day`() {
        val today = timestampForDaysAgo(0)
        val timestamps = listOf(
            today,
            today + TimeUnit.HOURS.toMillis(2),
            today + TimeUnit.HOURS.toMillis(5)
        )
        assertEquals(1, StreakCalculator.calculate(timestamps))
    }

    @Test
    fun `streak counts consecutive days ending yesterday`() {
        val timestamps = listOf(
            timestampForDaysAgo(1),
            timestampForDaysAgo(2),
            timestampForDaysAgo(3)
        )
        assertEquals(3, StreakCalculator.calculate(timestamps))
    }
}
