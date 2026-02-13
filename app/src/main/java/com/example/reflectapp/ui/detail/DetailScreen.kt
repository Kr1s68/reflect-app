package com.example.reflectapp.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.reflectapp.R
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.ui.components.ConfirmDeleteDialog
import com.example.reflectapp.ui.components.DateText

/**
 * Read-only detail view of a single journal entry.
 *
 * Displays the full entry content including title, mood, category, photo, content, and dates.
 * Provides Edit and Delete action buttons.
 *
 * @param entry The [JournalEntry] to display.
 * @param onEditClick Called when the Edit button is tapped.
 * @param onDeleteClick Called when delete is confirmed via the dialog.
 * @param onNavigateBack Called when the back button is tapped.
 * @param onFavoriteToggle Called when the favourite icon in the top bar is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    entry: JournalEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    val mood = entry.mood?.let { Mood.entries.getOrNull(it) }
    val category = entry.category?.let { name -> Category.entries.find { it.name == name } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.title_detail)) },
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
                            imageVector = if (entry.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(
                                if (entry.isFavorite) R.string.cd_remove_favorite else R.string.cd_add_favorite
                            ),
                            tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Mood + title
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (mood != null) {
                    Text(
                        text = mood.emoji,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dates
            Row {
                DateText(timestamp = entry.dateCreated, full = true)
            }
            if (entry.dateModified != entry.dateCreated) {
                Text(
                    text = stringResource(R.string.label_modified) + " " +
                        com.example.reflectapp.util.DateFormatter.formatFull(entry.dateModified),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Category chip
            if (category != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Content
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyLarge
            )

            // Photo
            if (entry.photoUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = entry.photoUri,
                    contentDescription = stringResource(R.string.cd_entry_photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.action_edit))
                }
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
