package com.example.reflectapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Mood

/**
 * A row of emoji-based chips for selecting the mood of a journal entry.
 *
 * @param selectedMood The currently selected [Mood], or null if none.
 * @param onMoodSelected Called with the selected [Mood], or null if the same mood is tapped again.
 * @param modifier Optional [Modifier].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.label_mood),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Mood.entries.forEach { mood ->
                FilterChip(
                    selected = selectedMood == mood,
                    onClick = {
                        onMoodSelected(if (selectedMood == mood) null else mood)
                    },
                    label = { Text("${mood.emoji} ${mood.label}") }
                )
            }
        }
    }
}
