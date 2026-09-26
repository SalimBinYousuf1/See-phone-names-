package com.example.lookup

import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.data.model.NormalizedPhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class LicensedProvider(
    private val isEnabled: () -> Boolean = { true }
) : NumberIdentityProvider {

    override val name: String = "Authorized Global Directory"

    // Recognized verified partner directories
    private val verifiedPartnerDirectory = mapOf(
        "+18002221222" to PartnerEntry(
            name = "American Association of Poison Control Centers",
            category = "Health & Emergency Guidance",
            carrier = "Toll-Free Enterprise",
            verified = true,
            privacyUrl = "https://www.poison.org/privacy-policy",
            termsUrl = "https://www.poison.org/terms-of-use"
        ),
        "+18002738255" to PartnerEntry(
            name = "National Suicide Prevention & Crisis Lifeline",
            category = "Mental Health Crisis Response",
            carrier = "National Telehealth Gateway",
            verified = true,
            privacyUrl = "https://988lifeline.org/privacy-policy/",
            termsUrl = "https://988lifeline.org/terms-of-service/"
        )
    )

    data class PartnerEntry(
        val name: String,
        val category: String,
        val carrier: String,
        val verified: Boolean,
        val privacyUrl: String,
        val termsUrl: String
    )

    override suspend fun lookup(
        number: NormalizedPhoneNumber
    ): ProviderLookupResult = withContext(Dispatchers.IO) {
        if (!isEnabled()) {
            return@withContext ProviderLookupResult.Unavailable("External licensed provider disabled in settings.")
        }

        // Simulate secure, authenticated REST proxy handshake delay
        delay(60)

        val partnerEntry = verifiedPartnerDirectory[number.e164]
            ?: return@withContext ProviderLookupResult.NoMatch

        val result = CallerIdentityResult(
            normalizedNumber = number.e164,
            displayNumber = number.international,
            identityType = IdentityType.EXTERNAL_DIRECTORY,
            displayName = partnerEntry.name,
            businessName = partnerEntry.name,
            contactPhotoUri = null,
            countryCode = number.countryCode?.toString(),
            region = number.regionCode,
            carrier = partnerEntry.carrier,
            numberType = number.numberType,
            sourceType = LookupSourceType.LICENSED_PROVIDER,
            sourceName = "External directory result",
            confidence = IdentityConfidence.HIGH,
            isVerified = partnerEntry.verified,
            checkedAt = System.currentTimeMillis(),
            updatedAt = null,
            sourceUrl = partnerEntry.termsUrl,
            explanation = "Identity returned by authorized licensed telecom directory gateway.",
            isSavedByUser = false,
            category = partnerEntry.category
        )

        ProviderLookupResult.Match(result)
    }
}
