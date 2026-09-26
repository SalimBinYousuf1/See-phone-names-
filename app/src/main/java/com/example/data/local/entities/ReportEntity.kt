package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lookup_reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val normalizedNumber: String,
    val reportedName: String?,
    val reportType: String,
    val reasonDescription: String,
    val reportedAt: Long = System.currentTimeMillis(),
    val status: String = "SUBMITTED"
)
