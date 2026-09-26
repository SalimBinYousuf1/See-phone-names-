package com.example.data.repository

import com.example.data.local.SalimDao
import com.example.data.local.entities.LookupHistoryEntity
import com.example.data.model.CallerIdentityResult
import kotlinx.coroutines.flow.Flow

class LookupHistoryRepository(private val salimDao: SalimDao) {

    val history: Flow<List<LookupHistoryEntity>> = salimDao.getLookupHistory()

    suspend fun recordLookup(
        rawInput: String,
        result: CallerIdentityResult
    ): Long {
        val entity = LookupHistoryEntity(
            rawInput = rawInput,
            normalizedNumber = result.normalizedNumber,
            displayNumber = result.displayNumber,
            displayName = result.displayName,
            businessName = result.businessName,
            identityType = result.identityType.name,
            sourceType = result.sourceType.name,
            sourceName = result.sourceName,
            confidence = result.confidence.name,
            isVerified = result.isVerified,
            lookedUpAt = System.currentTimeMillis()
        )
        return salimDao.insertLookupHistory(entity)
    }

    suspend fun deleteHistoryItem(id: Long) {
        salimDao.deleteLookupHistoryItem(id)
    }

    suspend fun clearHistory() {
        salimDao.clearLookupHistory()
    }

    suspend fun pruneOldHistory(retentionDays: Int) {
        val cutoff = System.currentTimeMillis() - (retentionDays.toLong() * 24 * 60 * 60 * 1000L)
        salimDao.deleteHistoryOlderThan(cutoff)
    }
}
