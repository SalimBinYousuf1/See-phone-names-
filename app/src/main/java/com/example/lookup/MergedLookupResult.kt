package com.example.lookup

import com.example.data.model.CallerIdentityResult

data class MergedLookupResult(
    val primaryResult: CallerIdentityResult,
    val alternativeMatches: List<CallerIdentityResult> = emptyList(),
    val hasConflict: Boolean = false,
    val conflictExplanation: String? = null
)
