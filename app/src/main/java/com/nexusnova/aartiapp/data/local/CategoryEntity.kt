package com.nexusnova.aartiapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String
)
