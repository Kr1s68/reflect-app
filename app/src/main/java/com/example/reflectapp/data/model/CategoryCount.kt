package com.example.reflectapp.data.model

/**
 * DAO query helper that maps a category name to its entry count.
 *
 * Used by [com.example.reflectapp.data.local.JournalDao.getCategoryCounts] to return
 * aggregated category statistics.
 */
data class CategoryCount(
    /** Name of the [Category] enum. */
    val category: String,

    /** Number of entries with this category. */
    val count: Int
)
