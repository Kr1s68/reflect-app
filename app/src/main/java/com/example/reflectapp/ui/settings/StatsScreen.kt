package com.example.reflectapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.CategoryCount
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.data.model.MoodCount

/**
 * Displays aggregated statistics about the user's journal entries.
 *
 * Shows total entry count, current writing streak, mood distribution, and category breakdown.
 *
 * @param totalEntries Total number of entries in the database.
 * @param streak Current consecutive-day writing streak.
 * @param moodCounts List of [MoodCount] objects for the mood distribution chart.
 * @param categoryCounts List of [CategoryCount] objects for the category breakdown.
 * @param isLoading True while data is being fetched.
 * @param modifier Optional [Modifier].
 */
@Composable
fun StatsScreen(
    totalEntries: Int,
    streak: Int,
    moodCounts: List<MoodCount>,
    categoryCounts: List<CategoryCount>,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Column(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Summary cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = stringResource(R.string.stats_total_entries),
                value = totalEntries.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.stats_streak),
                value = stringResource(R.string.stats_streak_days, streak),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mood distribution
        if (moodCounts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.stats_mood_distribution),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            val maxCount = moodCounts.maxOf { it.count }.coerceAtLeast(1)
            moodCounts.forEach { mc ->
                val mood = Mood.entries.getOrNull(mc.mood)
                if (mood != null) {
                    MoodDistributionRow(
                        emoji = mood.emoji,
                        label = mood.label,
                        count = mc.count,
                        fraction = mc.count.toFloat() / maxCount
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category breakdown
        if (categoryCounts.isNotEmpty()) {
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.stats_category_breakdown),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            val maxCount = categoryCounts.maxOf { it.count }.coerceAtLeast(1)
            categoryCounts.forEach { cc ->
                val category = Category.entries.find { it.name == cc.category }
                if (category != null) {
                    CategoryDistributionRow(
                        label = category.displayName,
                        colorHex = category.colorHex,
                        count = cc.count,
                        fraction = cc.count.toFloat() / maxCount
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun MoodDistributionRow(
    emoji: String,
    label: String,
    count: Int,
    fraction: Float
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryDistributionRow(
    label: String,
    colorHex: String,
    count: Int,
    fraction: Float
) {
    val color = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: IllegalArgumentException) {
        MaterialTheme.colorScheme.primary
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = color
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
