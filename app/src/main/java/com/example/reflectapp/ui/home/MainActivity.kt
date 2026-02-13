package com.example.reflectapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.reflectapp.ReflectApplication
import com.example.reflectapp.ui.detail.DetailActivity
import com.example.reflectapp.ui.settings.SettingsActivity
import com.example.reflectapp.ui.theme.ReflectTheme
import com.example.reflectapp.viewmodel.JournalViewModel
import com.example.reflectapp.viewmodel.ViewModelFactory

/**
 * The main entry-point Activity for the Reflect app.
 *
 * Hosts the [HomeScreen] composable and handles navigation to [DetailActivity]
 * (for creating or viewing entries) and [SettingsActivity] (for statistics/settings).
 *
 * Uses MVVM: state comes from [JournalViewModel], user interactions trigger ViewModel methods,
 * and the Room Flow keeps the list automatically up-to-date.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: JournalViewModel by viewModels {
        ViewModelFactory((application as ReflectApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReflectTheme {
                val entries by viewModel.entries.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val selectedMood by viewModel.selectedMood.collectAsState()
                val selectedCategory by viewModel.selectedCategory.collectAsState()

                HomeScreen(
                    entries = entries,
                    searchQuery = searchQuery,
                    selectedMood = selectedMood,
                    selectedCategory = selectedCategory,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onMoodFilterChanged = viewModel::onMoodFilterChanged,
                    onCategoryFilterChanged = viewModel::onCategoryFilterChanged,
                    onClearFilters = viewModel::clearFilters,
                    onEntryClick = { id -> navigateToDetail(id) },
                    onFavoriteClick = viewModel::toggleFavorite,
                    onAddClick = { navigateToDetail(null) },
                    onStatsClick = { startActivity(Intent(this, SettingsActivity::class.java)) }
                )
            }
        }
    }

    /**
     * Navigates to [DetailActivity].
     *
     * @param entryId The ID of the entry to view/edit, or null to create a new entry.
     */
    private fun navigateToDetail(entryId: Long?) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            if (entryId != null) {
                putExtra(DetailActivity.EXTRA_ENTRY_ID, entryId)
            }
        }
        startActivity(intent)
    }
}
