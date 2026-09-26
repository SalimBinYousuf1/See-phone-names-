package com.example.lookup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.data.model.NormalizedPhoneNumber
import com.example.util.PhoneNumberNormalizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsProvider(
    private val context: Context,
    private val normalizer: PhoneNumberNormalizer
) : NumberIdentityProvider {

    override val name: String = "Device Contacts"

    override suspend fun lookup(
        number: NormalizedPhoneNumber
    ): ProviderLookupResult = withContext(Dispatchers.IO) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return@withContext ProviderLookupResult.Unavailable(
                "Contact permission not granted. Contacts are never uploaded."
            )
        }

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        val matches = mutableListOf<CallerIdentityResult>()

        try {
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )
                val numberIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                val photoIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                )
                val labelIndex = cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.LABEL
                )

                while (cursor.moveToNext()) {
                    val rawContactNumber = cursor.getString(numberIndex) ?: continue

                    val normalizedContact = runCatching {
                        normalizer.normalize(
                            rawContactNumber,
                            number.regionCode ?: "US"
                        ).getOrNull()?.e164
                    }.getOrNull()

                    if (normalizedContact != null && normalizedContact == number.e164) {
                        val contactName = cursor.getString(nameIndex) ?: "Contact"
                        val photoUri = cursor.getString(photoIndex)
                        val customLabel = if (labelIndex >= 0) cursor.getString(labelIndex) else null

                        matches += CallerIdentityResult(
                            normalizedNumber = number.e164,
                            displayNumber = number.international,
                            identityType = IdentityType.PERSON_CONTACT,
                            displayName = contactName,
                            businessName = null,
                            contactPhotoUri = photoUri,
                            countryCode = number.countryCode?.toString(),
                            region = number.regionCode,
                            carrier = null,
                            numberType = number.numberType,
                            sourceType = LookupSourceType.DEVICE_CONTACTS,
                            sourceName = "Saved in your contacts",
                            confidence = IdentityConfidence.VERIFIED,
                            isVerified = true,
                            checkedAt = System.currentTimeMillis(),
                            updatedAt = null,
                            sourceUrl = null,
                            explanation = "This number matches a contact saved on your device.",
                            isSavedByUser = false,
                            notes = customLabel
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return@withContext ProviderLookupResult.Unavailable("Failed to query device contacts: ${e.message}")
        }

        val first = matches.firstOrNull() ?: return@withContext ProviderLookupResult.NoMatch
        ProviderLookupResult.Match(first)
    }
}
