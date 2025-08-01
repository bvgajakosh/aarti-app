package com.nexusnova.aartiapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AartiDao {
    @Query("SELECT * FROM aarti ORDER BY title")
    fun getAll(): Flow<List<AartiEntity>>

    @Query("SELECT * FROM aarti WHERE categoryId = :catId ORDER BY title")
    fun getByCategory(catId: String): Flow<List<AartiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(list: List<AartiEntity>)

    @Query("DELETE FROM aarti")
    fun clearAll(): Int
}
