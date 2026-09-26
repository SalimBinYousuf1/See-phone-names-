package com.example.domain.usecase

import com.example.data.repository.LookupHistoryRepository

class DeleteLookupHistoryUseCase(
    private val historyRepository: LookupHistoryRepository
) {
    suspend fun deleteItem(id: Long) {
        historyRepository.deleteHistoryItem(id)
    }

    suspend fun clearAll() {
        historyRepository.clearHistory()
    }
}
