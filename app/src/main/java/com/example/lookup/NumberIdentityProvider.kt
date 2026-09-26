package com.example.lookup

import com.example.data.model.CallerIdentityResult
import com.example.data.model.NormalizedPhoneNumber

sealed interface ProviderLookupResult {
    data class Match(
        val identity: CallerIdentityResult
    ) : ProviderLookupResult

    data object NoMatch : ProviderLookupResult

    data class Unavailable(
        val reason: String
    ) : ProviderLookupResult
}

interface NumberIdentityProvider {
    val name: String
    suspend fun lookup(number: NormalizedPhoneNumber): ProviderLookupResult
}
