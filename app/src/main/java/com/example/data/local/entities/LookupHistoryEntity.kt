package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lookup_history")
data class LookupHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawInput: String,
    val normalizedNumber: String,
    val displayNumber: String,
    val displayName: String?,
    val businessName: String?,
    val identityType: String,
    val sourceType: String,
    val sourceName: String?,
    val confidence: String,
    val isVerified: Boolean,
    val lookedUpAt: Long = System.currentTimeMillis()
)
