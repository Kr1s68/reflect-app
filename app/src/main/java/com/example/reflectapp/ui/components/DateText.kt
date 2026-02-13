package com.example.reflectapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.example.reflectapp.util.DateFormatter

/**
 * Displays a formatted date from a Unix timestamp.
 *
 * @param timestamp Unix timestamp in milliseconds to display.
 * @param modifier Optional [Modifier] applied to the [Text].
 * @param style Text style to apply; defaults to [MaterialTheme.typography.bodySmall].
 * @param full If true, uses a full date-time format (e.g. "January 5, 2025 at 3:45 PM");
 *             if false, uses a short date format (e.g. "Jan 5, 2025").
 */
@Composable
fun DateText(
    timestamp: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    full: Boolean = false
) {
    val formatted = if (full) DateFormatter.formatFull(timestamp) else DateFormatter.formatShort(timestamp)
    Text(
        text = formatted,
        style = style,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}
