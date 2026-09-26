package com.example.lookup

import com.example.data.local.SalimDao
import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.data.model.NormalizedPhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDirectoryProvider(
    private val salimDao: SalimDao
) : NumberIdentityProvider {

    override val name: String = "Local User Directory"

    override suspend fun lookup(
        number: NormalizedPhoneNumber
    ): ProviderLookupResult = withContext(Dispatchers.IO) {
        val saved = salimDao.findSavedNumber(number.e164) ?: return@withContext ProviderLookupResult.NoMatch

        val result = CallerIdentityResult(
            normalizedNumber = number.e164,
            displayNumber = if (saved.displayNumber.isNotBlank()) saved.displayNumber else number.international,
            identityType = IdentityType.USER_SAVED,
            displayName = saved.label,
            businessName = null,
            contactPhotoUri = null,
            countryCode = number.countryCode?.toString(),
            region = number.regionCode,
            carrier = null,
            numberType = number.numberType,
            sourceType = LookupSourceType.LOCAL_USER_DIRECTORY,
            sourceName = "Saved by you",
            confidence = IdentityConfidence.HIGH,
            isVerified = false, // Critical rule: user created labels are never displayed as Verified
            checkedAt = System.currentTimeMillis(),
            updatedAt = saved.updatedAt,
            sourceUrl = null,
            explanation = "This label was saved manually by you in your local directory.",
            isSavedByUser = true,
            notes = saved.notes,
            category = saved.category
        )

        ProviderLookupResult.Match(result)
    }
}
