package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_numbers",
    indices = [
        Index(value = ["normalizedNumber"], unique = true)
    ]
)
data class SavedNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val normalizedNumber: String,
    val displayNumber: String = "",
    val label: String,
    val notes: String? = null,
    val category: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
