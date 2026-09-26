package com.example

import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.IdentityType
import com.example.data.model.LookupSourceType
import com.example.lookup.ResultMerger
import com.example.util.PhoneNumberNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PhoneNumberNormalizerTest {

    private lateinit var normalizer: PhoneNumberNormalizer

    @Before
    fun setUp() {
        normalizer = PhoneNumberNormalizer()
    }

    @Test
    fun normalize_validUsNumber_formatsCorrectly() {
        val result = normalizer.normalize("8002752273", "US")
        assertTrue(result.isSuccess)
        val normalized = result.getOrThrow()
        assertEquals("+18002752273", normalized.e164)
        assertEquals(1, normalized.countryCode)
        assertEquals("US", normalized.regionCode)
        assertTrue(normalized.isValid)
        assertTrue(normalized.isPossible)
    }

    @Test
    fun normalize_validInternationalWithPlus_formatsCorrectly() {
        val result = normalizer.normalize("+44 20 7946 0000", "US")
        assertTrue(result.isSuccess)
        val normalized = result.getOrThrow()
        assertEquals("+442079460000", normalized.e164)
        assertEquals(44, normalized.countryCode)
        assertEquals("GB", normalized.regionCode)
    }

    @Test
    fun normalize_blankInput_returnsFailure() {
        val result = normalizer.normalize("   ", "US")
        assertTrue(result.isFailure)
    }

    @Test
    fun normalize_impossibleNumber_returnsFailure() {
        val result = normalizer.normalize("123", "US")
        assertTrue(result.isFailure)
    }

    @Test
    fun resultMerger_emptyMatches_returnsNoVerifiedIdentity() {
        val normalized = normalizer.normalize("+18005550199", "US").getOrThrow()
        val merged = ResultMerger.merge(normalized, emptyList())
        assertEquals(IdentityType.UNKNOWN, merged.primaryResult.identityType)
        assertFalse(merged.primaryResult.isVerified)
        assertEquals("No verified identity found.", merged.primaryResult.explanation)
    }

    @Test
    fun resultMerger_conflictingSources_detectsConflict() {
        val normalized = normalizer.normalize("+18005550199", "US").getOrThrow()
        val match1 = CallerIdentityResult(
            normalizedNumber = normalized.e164,
            displayNumber = normalized.international,
            identityType = IdentityType.USER_SAVED,
            displayName = "Alex Personal Cell",
            businessName = null,
            contactPhotoUri = null,
            countryCode = "1",
            region = "US",
            carrier = null,
            numberType = "MOBILE",
            sourceType = LookupSourceType.LOCAL_USER_DIRECTORY,
            sourceName = "Saved by you",
            confidence = IdentityConfidence.HIGH,
            isVerified = false,
            checkedAt = System.currentTimeMillis(),
            updatedAt = null,
            sourceUrl = null,
            explanation = "Saved by you."
        )
        val match2 = CallerIdentityResult(
            normalizedNumber = normalized.e164,
            displayNumber = normalized.international,
            identityType = IdentityType.COMMUNITY_SUBMISSION,
            displayName = "Apex Hardware Co.",
            businessName = "Apex Hardware Co.",
            contactPhotoUri = null,
            countryCode = "1",
            region = "US",
            carrier = null,
            numberType = "MOBILE",
            sourceType = LookupSourceType.COMMUNITY_DIRECTORY,
            sourceName = "Community-submitted",
            confidence = IdentityConfidence.LOW,
            isVerified = false,
            checkedAt = System.currentTimeMillis(),
            updatedAt = null,
            sourceUrl = null,
            explanation = "Community record."
        )

        val merged = ResultMerger.merge(normalized, listOf(match1, match2))
        assertTrue(merged.hasConflict)
        assertNotNull(merged.conflictExplanation)
        assertEquals(match1.displayName, merged.primaryResult.displayName)
    }

    @Test
    fun globalDirectoryEngine_resolvesIndefinitely() {
        val normalized = normalizer.normalize("+12125550198", "US").getOrThrow()
        val result = com.example.lookup.GlobalTelecomDirectoryEngine.resolveNumber(normalized)
        assertNotNull(result.displayName)
        assertTrue(result.displayName!!.isNotBlank())
        assertEquals("+12125550198", result.normalizedNumber)
        assertNotNull(result.carrier)
        assertNotNull(result.region)
        assertEquals(LookupSourceType.GLOBAL_DIRECTORY_INTELLIGENCE, result.sourceType)
    }
}
