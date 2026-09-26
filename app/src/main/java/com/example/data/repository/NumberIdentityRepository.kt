package com.example.data.repository

import com.example.data.model.NormalizedPhoneNumber
import com.example.lookup.CommunityDirectoryProvider
import com.example.lookup.ContactsProvider
import com.example.lookup.GlobalDirectoryIntelligenceProvider
import com.example.lookup.IdentityResolver
import com.example.lookup.LicensedProvider
import com.example.lookup.LocalDirectoryProvider
import com.example.lookup.MergedLookupResult
import com.example.lookup.NumberIdentityProvider
import com.example.lookup.PublicBusinessDirectoryProvider
import com.example.util.PhoneNumberNormalizer
import kotlinx.coroutines.flow.first

class NumberIdentityRepository(
    private val normalizer: PhoneNumberNormalizer,
    private val contactsProvider: ContactsProvider,
    private val localDirectoryProvider: LocalDirectoryProvider,
    private val publicBusinessDirectoryProvider: PublicBusinessDirectoryProvider,
    private val communityDirectoryProvider: CommunityDirectoryProvider,
    private val licensedProvider: LicensedProvider,
    private val globalDirectoryProvider: GlobalDirectoryIntelligenceProvider,
    private val historyRepository: LookupHistoryRepository,
    private val settingsRepository: SettingsRepository
) {

    fun normalizeNumber(rawInput: String, defaultRegion: String): Result<NormalizedPhoneNumber> {
        return normalizer.normalize(rawInput, defaultRegion)
    }

    suspend fun lookupIdentity(
        normalizedNumber: NormalizedPhoneNumber,
        saveToHistory: Boolean = true
    ): MergedLookupResult {
        val useContacts = settingsRepository.useDeviceContacts.first()
        val enableExternal = settingsRepository.enableAuthorizedProvider.first()

        val activeProviders = mutableListOf<NumberIdentityProvider>()

        // 1. Local contacts if enabled
        if (useContacts) {
            activeProviders.add(contactsProvider)
        }

        // 2. Local user-created directory
        activeProviders.add(localDirectoryProvider)

        // 3. Public business directory
        activeProviders.add(publicBusinessDirectoryProvider)

        // 4. Consented community directory
        activeProviders.add(communityDirectoryProvider)

        // 5. External licensed provider if enabled
        if (enableExternal) {
            activeProviders.add(licensedProvider)
        }

        // 6. Global Directory Intelligence (Truecaller-like indefinite live directory resolver)
        activeProviders.add(globalDirectoryProvider)

        val resolver = IdentityResolver(activeProviders)
        val merged = resolver.resolve(normalizedNumber)

        // Record history if requested and allowed
        val recordHistorySetting = settingsRepository.saveLookupHistory.first()
        if (saveToHistory && recordHistorySetting) {
            historyRepository.recordLookup(
                rawInput = normalizedNumber.rawInput,
                result = merged.primaryResult
            )
        }

        return merged
    }

    suspend fun deleteAllData() {
        historyRepository.clearHistory()
        localDirectoryProvider.lookup(
            NormalizedPhoneNumber(
                rawInput = "",
                e164 = "",
                international = "",
                national = "",
                countryCode = null,
                regionCode = null,
                numberType = null,
                isValid = false,
                isPossible = false
            )
        )
    }
}
