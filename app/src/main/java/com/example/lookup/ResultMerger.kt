package com.example.lookup

import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.data.model.NormalizedPhoneNumber

object ResultMerger {

    fun merge(
        number: NormalizedPhoneNumber,
        matches: List<CallerIdentityResult>
    ): MergedLookupResult {
        if (matches.isEmpty()) {
            val emptyResult = CallerIdentityResult(
                normalizedNumber = number.e164,
                displayNumber = number.international,
                identityType = IdentityType.UNKNOWN,
                displayName = null,
                businessName = null,
                contactPhotoUri = null,
                countryCode = number.countryCode?.toString(),
                region = number.regionCode,
                carrier = null,
                numberType = number.numberType,
                sourceType = LookupSourceType.NONE,
                sourceName = null,
                confidence = IdentityConfidence.UNKNOWN,
                isVerified = false,
                checkedAt = System.currentTimeMillis(),
                updatedAt = null,
                sourceUrl = null,
                explanation = "No verified identity found.",
                isSavedByUser = false
            )
            return MergedLookupResult(primaryResult = emptyResult)
        }

        // Sort strictly by source precedence:
        // 1. Device Contacts (user's personal address book)
        // 2. Local User Directory (user's personal saved labels)
        // 3. Public Business Directory (official public registry)
        // 4. Consented Community Directory
        // 5. Licensed External Provider
        val sorted = matches.sortedBy { match ->
            when (match.sourceType) {
                LookupSourceType.DEVICE_CONTACTS -> 1
                LookupSourceType.LOCAL_USER_DIRECTORY -> 2
                LookupSourceType.PUBLIC_BUSINESS_DIRECTORY -> 3
                LookupSourceType.COMMUNITY_DIRECTORY -> 4
                LookupSourceType.LICENSED_PROVIDER -> 5
                LookupSourceType.GLOBAL_DIRECTORY_INTELLIGENCE -> 6
                LookupSourceType.NONE -> 7
            }
        }

        val primary = sorted.first()
        val distinctNames = sorted.mapNotNull { it.displayName?.trim()?.lowercase() }.distinct()
        val hasConflict = distinctNames.size > 1

        val conflictExplanation = if (hasConflict) {
            "Multiple sources report conflicting identities for this phone number. Rather than guessing, all verified and reported source records are displayed below for your review."
        } else null

        return MergedLookupResult(
            primaryResult = primary,
            alternativeMatches = if (sorted.size > 1) sorted.drop(1) else emptyList(),
            hasConflict = hasConflict,
            conflictExplanation = conflictExplanation
        )
    }
}
