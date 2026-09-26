package com.example.lookup

import com.example.data.model.NormalizedPhoneNumber
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class IdentityResolver(
    private val providers: List<NumberIdentityProvider>
) {

    suspend fun resolve(
        number: NormalizedPhoneNumber
    ): MergedLookupResult = coroutineScope {
        // Query providers concurrently
        val matchDeferreds = providers.map { provider ->
            async {
                runCatching {
                    provider.lookup(number)
                }.getOrNull()
            }
        }

        val results = matchDeferreds.awaitAll()

        val matches = results
            .filterIsInstance<ProviderLookupResult.Match>()
            .map { it.identity }

        ResultMerger.merge(number, matches)
    }
}
