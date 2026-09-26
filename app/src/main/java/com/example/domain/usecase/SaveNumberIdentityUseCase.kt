package com.example.domain.usecase

import com.example.data.repository.LocalDirectoryRepository

class SaveNumberIdentityUseCase(
    private val localDirectoryRepository: LocalDirectoryRepository
) {
    suspend operator fun invoke(
        normalizedNumber: String,
        displayNumber: String,
        label: String,
        notes: String? = null,
        category: String? = null,
        isFavorite: Boolean = false
    ): Long {
        return localDirectoryRepository.saveNumber(
            normalizedNumber = normalizedNumber,
            displayNumber = displayNumber,
            label = label,
            notes = notes,
            category = category,
            isFavorite = isFavorite
        )
    }
}
