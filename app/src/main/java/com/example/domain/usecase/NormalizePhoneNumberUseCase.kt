package com.example.domain.usecase

import com.example.data.model.NormalizedPhoneNumber
import com.example.data.repository.NumberIdentityRepository

class NormalizePhoneNumberUseCase(
    private val repository: NumberIdentityRepository
) {
    operator fun invoke(rawInput: String, defaultRegion: String): Result<NormalizedPhoneNumber> {
        return repository.normalizeNumber(rawInput, defaultRegion)
    }
}
