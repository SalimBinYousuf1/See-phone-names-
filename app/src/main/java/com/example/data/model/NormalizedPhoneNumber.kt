package com.example.data.model

data class NormalizedPhoneNumber(
    val rawInput: String,
    val e164: String,
    val international: String,
    val national: String,
    val countryCode: Int?,
    val regionCode: String?,
    val numberType: String?,
    val isValid: Boolean,
    val isPossible: Boolean
)
