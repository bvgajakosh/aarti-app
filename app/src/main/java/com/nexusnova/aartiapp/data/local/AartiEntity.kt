package com.nexusnova.aartiapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aarti")
data class AartiEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageURL: String,
    val mp3URL: String,
    val prime: String,
    val categoryId: String
)

