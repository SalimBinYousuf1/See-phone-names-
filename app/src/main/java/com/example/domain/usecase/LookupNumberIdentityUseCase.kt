package com.example.domain.usecase

import com.example.data.model.NormalizedPhoneNumber
import com.example.data.repository.NumberIdentityRepository
import com.example.lookup.MergedLookupResult

class LookupNumberIdentityUseCase(
    private val repository: NumberIdentityRepository
) {
    suspend operator fun invoke(
        normalizedNumber: NormalizedPhoneNumber,
        saveToHistory: Boolean = true
    ): MergedLookupResult {
        return repository.lookupIdentity(normalizedNumber, saveToHistory)
    }
}
