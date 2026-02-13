package com.example.reflectapp.ui.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.reflectapp.R

/**
 * Displays the photo attachment section in the entry edit form.
 *
 * If [photoUri] is non-null, shows the attached image with a remove button.
 * If null, shows a button to pick a photo from the gallery.
 *
 * @param photoUri The URI string of the currently attached photo, or null.
 * @param onPhotoSelected Called with the new photo URI string when a photo is picked.
 * @param onPhotoRemoved Called when the remove button is tapped.
 * @param modifier Optional [Modifier].
 */
@Composable
fun PhotoSection(
    photoUri: String?,
    onPhotoSelected: (String) -> Unit,
    onPhotoRemoved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoSelected(it.toString()) }
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.label_photo),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (photoUri != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = stringResource(R.string.cd_entry_photo),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onPhotoRemoved,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_remove_photo),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        } else {
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp)
                )
                Text(text = stringResource(R.string.action_add_photo))
            }
        }
    }
}
