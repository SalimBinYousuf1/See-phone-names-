package com.example.util

import com.example.data.model.NormalizedPhoneNumber
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

class PhoneNumberNormalizer(
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
) {

    fun normalize(
        rawInput: String,
        defaultRegion: String = "US"
    ): Result<NormalizedPhoneNumber> {
        val cleaned = rawInput.trim()

        if (cleaned.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Please enter a phone number.")
            )
        }

        return runCatching {
            // Handle regional or leading + parsing
            val parsed = phoneUtil.parse(cleaned, defaultRegion)
            val isPossible = phoneUtil.isPossibleNumber(parsed)
            val isValid = phoneUtil.isValidNumber(parsed)

            if (!isPossible) {
                throw IllegalArgumentException("The phone number length or structure is impossible.")
            }

            val e164 = if (isValid) {
                phoneUtil.format(parsed, PhoneNumberFormat.E164)
            } else {
                // For partially possible numbers or edge formats, preserve standard E164-like formatting
                "+${parsed.countryCode}${parsed.nationalNumber}"
            }

            val international = phoneUtil.format(parsed, PhoneNumberFormat.INTERNATIONAL)
            val national = phoneUtil.format(parsed, PhoneNumberFormat.NATIONAL)
            val region = phoneUtil.getRegionCodeForNumber(parsed) ?: defaultRegion

            NormalizedPhoneNumber(
                rawInput = rawInput,
                e164 = e164,
                international = international,
                national = national,
                countryCode = parsed.countryCode,
                regionCode = region,
                numberType = phoneUtil.getNumberType(parsed).name,
                isValid = isValid,
                isPossible = isPossible
            )
        }
    }

    fun getSupportedRegions(): List<RegionInfo> {
        val regions = phoneUtil.supportedRegions
        return regions.map { code ->
            val countryCode = phoneUtil.getCountryCodeForRegion(code)
            RegionInfo(
                regionCode = code,
                countryCode = countryCode,
                displayName = java.util.Locale("", code).displayCountry.ifBlank { code }
            )
        }.sortedBy { it.displayName }
    }
}

data class RegionInfo(
    val regionCode: String,
    val countryCode: Int,
    val displayName: String
)
