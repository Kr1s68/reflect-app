package com.example.reflectapp.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.Mood

/**
 * The edit/create screen for a journal entry.
 *
 * Shown when creating a new entry or editing an existing one.
 * All field values are driven by the ViewModel via state parameters.
 *
 * @param isNew True if creating a new entry, false if editing an existing one.
 * @param title Current value of the title field.
 * @param content Current value of the content field.
 * @param mood Currently selected [Mood], or null.
 * @param category Currently selected [Category], or null.
 * @param photoUri Currently attached photo URI string, or null.
 * @param isFavorite Whether the entry is marked as favourite.
 * @param onTitleChanged Called when title text changes.
 * @param onContentChanged Called when content text changes.
 * @param onMoodSelected Called when a mood chip is tapped.
 * @param onCategorySelected Called when a category chip is tapped.
 * @param onPhotoSelected Called when a photo is picked from the gallery.
 * @param onPhotoRemoved Called when the photo is removed.
 * @param onFavoriteToggle Called when the favourite icon is tapped.
 * @param onSave Called when the save FAB is tapped.
 * @param onNavigateBack Called when the back button is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    isNew: Boolean,
    title: String,
    content: String,
    mood: Mood?,
    category: Category?,
    photoUri: String?,
    isFavorite: Boolean,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onMoodSelected: (Mood?) -> Unit,
    onCategorySelected: (Category?) -> Unit,
    onPhotoSelected: (String) -> Unit,
    onPhotoRemoved: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isNew) stringResource(R.string.title_new_entry)
                        else stringResource(R.string.title_edit_entry)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(
                                if (isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite
                            ),
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSave,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.cd_save_entry)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                label = { Text(stringResource(R.string.label_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Content field
            OutlinedTextField(
                value = content,
                onValueChange = onContentChanged,
                label = { Text(stringResource(R.string.label_content)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                maxLines = 10,
                isError = content.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood selector
            MoodSelector(
                selectedMood = mood,
                onMoodSelected = onMoodSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category selector
            CategorySelector(
                selectedCategory = category,
                onCategorySelected = onCategorySelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Photo section
            PhotoSection(
                photoUri = photoUri,
                onPhotoSelected = onPhotoSelected,
                onPhotoRemoved = onPhotoRemoved
            )

            // Extra space for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
