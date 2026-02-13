package com.example.reflectapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reflectapp.data.repository.JournalRepository

/**
 * A [ViewModelProvider.Factory] that injects a [JournalRepository] into ViewModels.
 *
 * Supports creating [JournalViewModel], [DetailViewModel], and [StatsViewModel].
 * Pass an instance of this factory to [androidx.activity.viewModels] or
 * [androidx.fragment.app.viewModels] to supply the repository dependency.
 *
 * @property repository The shared repository instance provided by [com.example.reflectapp.ReflectApplication].
 */
class ViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JournalViewModel::class.java) ->
                JournalViewModel(repository) as T

            modelClass.isAssignableFrom(DetailViewModel::class.java) ->
                DetailViewModel(repository) as T

            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(repository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
