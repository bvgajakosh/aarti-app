package com.nexusnova.aartiapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.jvm.Volatile

@Database(
    entities = [AartiEntity::class, CategoryEntity::class],
    version = 2,                  // ← bump version from 1 to 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun aartiDao(): AartiDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aarti_db"
                )
                    .fallbackToDestructiveMigration()  // ← drop & re-create on version mismatch
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
