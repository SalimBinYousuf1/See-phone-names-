package com.example.data.repository

import com.example.data.local.SalimDao
import com.example.data.local.entities.SavedNumberEntity
import kotlinx.coroutines.flow.Flow

class LocalDirectoryRepository(private val salimDao: SalimDao) {

    val savedNumbers: Flow<List<SavedNumberEntity>> = salimDao.getAllSavedNumbers()

    suspend fun findSavedNumber(normalizedNumber: String): SavedNumberEntity? {
        return salimDao.findSavedNumber(normalizedNumber)
    }

    suspend fun saveNumber(
        normalizedNumber: String,
        displayNumber: String,
        label: String,
        notes: String? = null,
        category: String? = null,
        isFavorite: Boolean = false
    ): Long {
        val entity = SavedNumberEntity(
            normalizedNumber = normalizedNumber,
            displayNumber = displayNumber,
            label = label.trim(),
            notes = notes?.trim()?.ifBlank { null },
            category = category?.trim()?.ifBlank { null },
            isFavorite = isFavorite,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return salimDao.insertOrUpdateSavedNumber(entity)
    }

    suspend fun deleteSavedNumber(entity: SavedNumberEntity) {
        salimDao.deleteSavedNumber(entity)
    }

    suspend fun deleteByNumber(normalizedNumber: String) {
        salimDao.deleteSavedNumberByNumber(normalizedNumber)
    }

    suspend fun clearAll() {
        salimDao.clearAllSavedNumbers()
    }
}
