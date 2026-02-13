package com.example.reflectapp.ui.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.reflectapp.ReflectApplication
import com.example.reflectapp.ui.theme.ReflectTheme
import com.example.reflectapp.viewmodel.DetailViewModel
import com.example.reflectapp.viewmodel.ViewModelFactory

/**
 * Activity hosting the journal entry detail and edit screens.
 *
 * Launched from [com.example.reflectapp.ui.home.MainActivity] with an optional
 * [EXTRA_ENTRY_ID]. If the extra is absent, the activity starts in "create new entry" mode.
 *
 * The activity manages two modes driven by [DetailViewModel]:
 * - **Detail mode** — read-only view of an existing entry ([DetailScreen]).
 * - **Edit mode** — editable form ([EditScreen]) for creating or updating an entry.
 */
class DetailActivity : ComponentActivity() {

    companion object {
        /** Intent extra key for the entry ID to view/edit. Pass -1L (or omit) to create new. */
        const val EXTRA_ENTRY_ID = "extra_entry_id"
        private const val NO_ID = -1L
    }

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory((application as ReflectApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val entryId = intent.getLongExtra(EXTRA_ENTRY_ID, NO_ID)
        val isNewEntry = entryId == NO_ID

        if (!isNewEntry) {
            viewModel.loadEntry(entryId)
        }

        setContent {
            ReflectTheme {
                val entry by viewModel.entry.collectAsState()
                val title by viewModel.title.collectAsState()
                val content by viewModel.content.collectAsState()
                val mood by viewModel.mood.collectAsState()
                val category by viewModel.category.collectAsState()
                val photoUri by viewModel.photoUri.collectAsState()
                val isFavorite by viewModel.isFavorite.collectAsState()
                val saveSuccess by viewModel.saveSuccess.collectAsState()

                // Mode: show edit screen when new, or when entry is loaded but user switches to edit
                var isEditing by remember { mutableStateOf(isNewEntry) }

                // When save/delete completes, finish the activity
                if (saveSuccess) {
                    finish()
                    return@ReflectTheme
                }

                // Unsaved changes confirmation dialog
                var showDiscardDialog by remember { mutableStateOf(false) }

                if (showDiscardDialog) {
                    AlertDialog(
                        onDismissRequest = { showDiscardDialog = false },
                        title = { Text("Discard changes?") },
                        text = { Text("You have unsaved changes. Do you want to discard them?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDiscardDialog = false
                                finish()
                            }) { Text("Discard") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDiscardDialog = false }) { Text("Keep editing") }
                        }
                    )
                }

                val handleBack: () -> Unit = {
                    if (isEditing && viewModel.hasUnsavedChanges()) {
                        showDiscardDialog = true
                    } else if (isEditing && !isNewEntry) {
                        isEditing = false
                    } else {
                        finish()
                    }
                }

                if (isEditing || (isNewEntry && entry == null)) {
                    EditScreen(
                        isNew = isNewEntry,
                        title = title,
                        content = content,
                        mood = mood,
                        category = category,
                        photoUri = photoUri,
                        isFavorite = isFavorite,
                        onTitleChanged = viewModel::onTitleChanged,
                        onContentChanged = viewModel::onContentChanged,
                        onMoodSelected = viewModel::onMoodSelected,
                        onCategorySelected = viewModel::onCategorySelected,
                        onPhotoSelected = viewModel::onPhotoUriChanged,
                        onPhotoRemoved = { viewModel.onPhotoUriChanged(null) },
                        onFavoriteToggle = viewModel::toggleFavorite,
                        onSave = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                viewModel.save()
                            }
                        },
                        onNavigateBack = handleBack
                    )
                } else {
                    val loadedEntry = entry
                    if (loadedEntry != null) {
                        DetailScreen(
                            entry = loadedEntry,
                            onEditClick = { isEditing = true },
                            onDeleteClick = { viewModel.delete() },
                            onNavigateBack = { finish() },
                            onFavoriteToggle = viewModel::toggleFavorite
                        )
                    }
                }
            }
        }
    }
}
