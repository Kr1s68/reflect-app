package com.example.reflectapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.Mood

/**
 * A modal bottom sheet that lets the user filter journal entries by mood or category.
 *
 * @param sheetState The [SheetState] controlling open/close animations.
 * @param selectedMood The currently active mood filter, or null if none.
 * @param selectedCategory The currently active category filter, or null if none.
 * @param onMoodSelected Called when a mood chip is tapped. Pass null to clear the mood filter.
 * @param onCategorySelected Called when a category chip is tapped. Pass null to clear.
 * @param onClearFilters Called when the "Clear all" button is tapped.
 * @param onDismiss Called when the bottom sheet is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    selectedMood: Mood?,
    selectedCategory: Category?,
    onMoodSelected: (Mood?) -> Unit,
    onCategorySelected: (Category?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(R.string.label_filter_by),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Mood filter chips
            Text(
                text = stringResource(R.string.label_mood),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
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

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter chips
            Text(
                text = stringResource(R.string.label_category),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            onCategorySelected(if (selectedCategory == category) null else category)
                        },
                        label = { Text(category.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.action_clear_filters))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
