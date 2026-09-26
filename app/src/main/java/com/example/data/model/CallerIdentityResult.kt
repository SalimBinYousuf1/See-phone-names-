package com.example.data.model

enum class IdentityType {
    PERSON_CONTACT,
    BUSINESS,
    USER_SAVED,
    COMMUNITY_SUBMISSION,
    EXTERNAL_DIRECTORY,
    UNKNOWN
}

enum class IdentityConfidence {
    VERIFIED,
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN
}

enum class LookupSourceType {
    DEVICE_CONTACTS,
    LOCAL_USER_DIRECTORY,
    PUBLIC_BUSINESS_DIRECTORY,
    COMMUNITY_DIRECTORY,
    LICENSED_PROVIDER,
    NONE
}

data class CallerIdentityResult(
    val normalizedNumber: String,
    val displayNumber: String,
    val identityType: IdentityType,
    val displayName: String?,
    val businessName: String?,
    val contactPhotoUri: String?,
    val countryCode: String?,
    val region: String?,
    val carrier: String?,
    val numberType: String?,
    val sourceType: LookupSourceType,
    val sourceName: String?,
    val confidence: IdentityConfidence,
    val isVerified: Boolean,
    val checkedAt: Long,
    val updatedAt: Long?,
    val sourceUrl: String?,
    val explanation: String,
    val isSavedByUser: Boolean = false,
    val notes: String? = null,
    val category: String? = null,
    val address: String? = null,
    val website: String? = null
)
