package com.example.reflectapp.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import kotlinx.coroutines.launch

/**
 * The main home screen composable showing the list of journal entries.
 *
 * Contains the TopAppBar, search bar, filter bottom sheet, entry list, and FAB.
 *
 * @param entries The list of [JournalEntry] objects to display.
 * @param searchQuery The current search query string.
 * @param selectedMood The currently active mood filter.
 * @param selectedCategory The currently active category filter.
 * @param onSearchQueryChanged Called when the search text changes.
 * @param onMoodFilterChanged Called when a mood filter chip is selected/deselected.
 * @param onCategoryFilterChanged Called when a category filter chip is selected/deselected.
 * @param onClearFilters Called to clear all active filters.
 * @param onEntryClick Called when an entry card is tapped (passes the entry ID).
 * @param onFavoriteClick Called when the favourite button on a card is tapped.
 * @param onAddClick Called when the FAB is tapped to create a new entry.
 * @param onStatsClick Called when the statistics icon in the top bar is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    entries: List<JournalEntry>,
    searchQuery: String,
    selectedMood: Mood?,
    selectedCategory: Category?,
    onSearchQueryChanged: (String) -> Unit,
    onMoodFilterChanged: (Mood?) -> Unit,
    onCategoryFilterChanged: (Category?) -> Unit,
    onClearFilters: () -> Unit,
    onEntryClick: (Long) -> Unit,
    onFavoriteClick: (JournalEntry) -> Unit,
    onAddClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showFilterSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val isFiltered = searchQuery.isNotBlank() || selectedMood != null || selectedCategory != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name_reflect),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch { sheetState.show() }
                        showFilterSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.cd_filter)
                        )
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = stringResource(R.string.cd_statistics)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_entry)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (entries.isEmpty()) {
                EmptyStateView(isFiltered = isFiltered)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    item {
                        JournalSearchBar(
                            query = searchQuery,
                            onQueryChanged = onSearchQueryChanged
                        )
                    }
                    items(entries, key = { it.id }) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry.id) },
                            onFavoriteClick = { onFavoriteClick(entry) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                sheetState = sheetState,
                selectedMood = selectedMood,
                selectedCategory = selectedCategory,
                onMoodSelected = onMoodFilterChanged,
                onCategorySelected = onCategoryFilterChanged,
                onClearFilters = {
                    onClearFilters()
                    scope.launch { sheetState.hide() }
                    showFilterSheet = false
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    showFilterSheet = false
                }
            )
        }
    }
}
