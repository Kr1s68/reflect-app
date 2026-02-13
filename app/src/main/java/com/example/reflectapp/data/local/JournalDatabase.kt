package com.example.reflectapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.reflectapp.data.model.JournalEntry

/**
 * Room database singleton for the Reflect app.
 *
 * Contains the [JournalEntry] entity and exposes [JournalDao] for data access.
 * Use [getInstance] to obtain the single database instance.
 */
@Database(
    entities = [JournalEntry::class],
    version = 1,
    exportSchema = false
)
abstract class JournalDatabase : RoomDatabase() {

    /** Returns the DAO used to perform operations on journal entries. */
    abstract fun journalDao(): JournalDao

    companion object {
        private const val DATABASE_NAME = "reflect_journal_db"

        @Volatile
        private var instance: JournalDatabase? = null

        /**
         * Returns the singleton [JournalDatabase] instance.
         *
         * Creates the database on first call using Room's in-process builder.
         * Thread-safe via double-checked locking.
         *
         * @param context Application context used to create the database.
         */
        fun getInstance(context: Context): JournalDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
        }
    }
}
