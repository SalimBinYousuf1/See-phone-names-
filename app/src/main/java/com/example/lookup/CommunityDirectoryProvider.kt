package com.example.lookup

import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.data.model.NormalizedPhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommunityDirectoryProvider : NumberIdentityProvider {

    override val name: String = "Consented Community Directory"

    data class CommunityEntry(
        val e164: String,
        val submittedName: String,
        val category: String,
        val submittedAt: Long,
        val submitterConsented: Boolean,
        val disputeCount: Int = 0
    )

    // Consented community listing examples where contributors gave express permission
    private val communityRecords = listOf(
        CommunityEntry(
            e164 = "+18005550199",
            submittedName = "Metropolis City Electric Co-Op Helpdesk",
            category = "Utility & Energy Services",
            submittedAt = 1750000000000L,
            submitterConsented = true,
            disputeCount = 0
        ),
        CommunityEntry(
            e164 = "+18005550144",
            submittedName = "Community Food Pantry Distribution Center",
            category = "Non-Profit & Community Assistance",
            submittedAt = 1755000000000L,
            submitterConsented = true,
            disputeCount = 0
        )
    )

    override suspend fun lookup(
        number: NormalizedPhoneNumber
    ): ProviderLookupResult = withContext(Dispatchers.IO) {
        val entry = communityRecords.find { it.e164 == number.e164 }
            ?: return@withContext ProviderLookupResult.NoMatch

        val result = CallerIdentityResult(
            normalizedNumber = number.e164,
            displayNumber = number.international,
            identityType = IdentityType.COMMUNITY_SUBMISSION,
            displayName = entry.submittedName,
            businessName = entry.submittedName,
            contactPhotoUri = null,
            countryCode = number.countryCode?.toString(),
            region = number.regionCode,
            carrier = null,
            numberType = number.numberType,
            sourceType = LookupSourceType.COMMUNITY_DIRECTORY,
            sourceName = "Community-submitted",
            confidence = IdentityConfidence.LOW, // Not confirmed, always marked low/medium
            isVerified = false, // Critical rule: never display as verified
            checkedAt = System.currentTimeMillis(),
            updatedAt = entry.submittedAt,
            sourceUrl = null,
            explanation = "Community-submitted record. Verification pending review. Submitter consented.",
            isSavedByUser = false,
            category = entry.category,
            notes = "Contributed on ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date(entry.submittedAt))}. Subject to dispute or removal."
        )

        ProviderLookupResult.Match(result)
    }
}
