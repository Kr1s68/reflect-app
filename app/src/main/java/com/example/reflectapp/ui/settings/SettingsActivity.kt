package com.example.reflectapp.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.reflectapp.R
import com.example.reflectapp.ReflectApplication
import com.example.reflectapp.ui.theme.ReflectTheme
import com.example.reflectapp.viewmodel.StatsViewModel
import com.example.reflectapp.viewmodel.ViewModelFactory

/**
 * Activity hosting the Statistics and Settings screens in a tabbed layout.
 *
 * Uses [StatsViewModel] to load and display aggregated journal statistics.
 * Provides a dark mode toggle that overrides the system theme within the session.
 */
@OptIn(ExperimentalMaterial3Api::class)
class SettingsActivity : ComponentActivity() {

    private val viewModel: StatsViewModel by viewModels {
        ViewModelFactory((application as ReflectApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            ReflectTheme(darkTheme = isDarkTheme) {
                val totalEntries by viewModel.totalEntries.collectAsState()
                val streak by viewModel.streak.collectAsState()
                val moodCounts by viewModel.moodCounts.collectAsState()
                val categoryCounts by viewModel.categoryCounts.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf(
                    stringResource(R.string.tab_statistics),
                    stringResource(R.string.tab_settings)
                )

                // Load stats when the screen first appears
                LaunchedEffect(Unit) {
                    viewModel.loadStats()
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = if (selectedTab == 0) stringResource(R.string.title_statistics)
                                    else stringResource(R.string.title_settings)
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.cd_navigate_back)
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        ScrollableTabRow(selectedTabIndex = selectedTab) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(text = title) }
                                )
                            }
                        }

                        when (selectedTab) {
                            0 -> StatsScreen(
                                totalEntries = totalEntries,
                                streak = streak,
                                moodCounts = moodCounts,
                                categoryCounts = categoryCounts,
                                isLoading = isLoading
                            )
                            1 -> SettingsScreen(
                                isDarkTheme = isDarkTheme,
                                onDarkThemeToggle = { isDarkTheme = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
