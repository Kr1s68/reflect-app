package com.example.reflectapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reflectapp.data.model.Category
import com.example.reflectapp.data.model.JournalEntry
import com.example.reflectapp.data.model.Mood
import com.example.reflectapp.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for [com.example.reflectapp.ui.detail.DetailActivity].
 *
 * Handles loading a single journal entry, tracking edit-form state, and
 * persisting create/update/delete operations to the repository.
 *
 * @property repository The data source for journal entries.
 */
class DetailViewModel(private val repository: JournalRepository) : ViewModel() {

    // ---- Loaded entry (read-only view) ----

    private val _entry = MutableStateFlow<JournalEntry?>(null)
    /** The journal entry currently being viewed or edited, or null if creating a new one. */
    val entry: StateFlow<JournalEntry?> = _entry.asStateFlow()

    // ---- Edit form state ----

    private val _title = MutableStateFlow("")
    /** The title field value in the edit form. */
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    /** The content field value in the edit form. */
    val content: StateFlow<String> = _content.asStateFlow()

    private val _mood = MutableStateFlow<Mood?>(null)
    /** The selected mood in the edit form, or null if none selected. */
    val mood: StateFlow<Mood?> = _mood.asStateFlow()

    private val _category = MutableStateFlow<Category?>(null)
    /** The selected category in the edit form, or null if none selected. */
    val category: StateFlow<Category?> = _category.asStateFlow()

    private val _photoUri = MutableStateFlow<String?>(null)
    /** The URI of the attached photo, or null if no photo is attached. */
    val photoUri: StateFlow<String?> = _photoUri.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    /** Whether the entry is marked as favourite in the edit form. */
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // ---- Operation result ----

    private val _saveSuccess = MutableStateFlow(false)
    /** Emits true when a save or delete operation completes successfully. */
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    // ---- Load ----

    /**
     * Loads an existing entry by [id] and populates the edit form fields.
     * If [id] is -1 or not found, the form remains in "create new" mode.
     *
     * @param id The ID of the entry to load.
     */
    fun loadEntry(id: Long) {
        viewModelScope.launch {
            repository.getEntryById(id).collect { e ->
                _entry.value = e
                if (e != null) {
                    _title.value = e.title
                    _content.value = e.content
                    _mood.value = e.mood?.let { Mood.entries.getOrNull(it) }
                    _category.value = e.category?.let { name ->
                        Category.entries.find { it.name == name }
                    }
                    _photoUri.value = e.photoUri
                    _isFavorite.value = e.isFavorite
                }
            }
        }
    }

    // ---- Form field updates ----

    /** Updates the title field. */
    fun onTitleChanged(value: String) { _title.value = value }

    /** Updates the content field. */
    fun onContentChanged(value: String) { _content.value = value }

    /** Updates the selected mood. */
    fun onMoodSelected(mood: Mood?) { _mood.value = mood }

    /** Updates the selected category. */
    fun onCategorySelected(category: Category?) { _category.value = category }

    /** Updates the attached photo URI. */
    fun onPhotoUriChanged(uri: String?) { _photoUri.value = uri }

    /** Toggles the favourite flag. */
    fun toggleFavorite() { _isFavorite.value = !_isFavorite.value }

    // ---- Persistence ----

    /**
     * Saves the current form state.
     *
     * If an entry is loaded ([entry] is not null), updates the existing record.
     * Otherwise, inserts a new entry. Sets [saveSuccess] to true on completion.
     */
    fun save() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val existing = _entry.value
            if (existing != null) {
                repository.update(
                    existing.copy(
                        title = _title.value.trim(),
                        content = _content.value.trim(),
                        dateModified = now,
                        mood = _mood.value?.ordinal,
                        category = _category.value?.name,
                        photoUri = _photoUri.value,
                        isFavorite = _isFavorite.value
                    )
                )
            } else {
                repository.insert(
                    JournalEntry(
                        title = _title.value.trim(),
                        content = _content.value.trim(),
                        dateCreated = now,
                        dateModified = now,
                        mood = _mood.value?.ordinal,
                        category = _category.value?.name,
                        photoUri = _photoUri.value,
                        isFavorite = _isFavorite.value
                    )
                )
            }
            _saveSuccess.value = true
        }
    }

    /**
     * Deletes the currently loaded entry.
     * If no entry is loaded, this is a no-op.
     * Sets [saveSuccess] to true on completion so the Activity can navigate back.
     */
    fun delete() {
        viewModelScope.launch {
            _entry.value?.let {
                repository.delete(it)
                _saveSuccess.value = true
            }
        }
    }

    /**
     * Returns true if the form has unsaved changes compared to the loaded entry,
     * or if fields are non-empty in create mode.
     */
    fun hasUnsavedChanges(): Boolean {
        val e = _entry.value
        return if (e == null) {
            _title.value.isNotBlank() || _content.value.isNotBlank()
        } else {
            e.title != _title.value.trim() ||
                e.content != _content.value.trim() ||
                e.mood != _mood.value?.ordinal ||
                e.category != _category.value?.name ||
                e.photoUri != _photoUri.value ||
                e.isFavorite != _isFavorite.value
        }
    }
}
