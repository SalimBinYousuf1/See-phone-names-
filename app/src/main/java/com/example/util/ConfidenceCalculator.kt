package com.example.util

import androidx.compose.ui.graphics.Color
import com.example.data.model.CallerIdentityResult
import com.example.data.model.IdentityConfidence
import com.example.data.model.LookupSourceType
import com.example.ui.theme.SalimBlue
import com.example.ui.theme.SalimGreen
import com.example.ui.theme.SalimOrange
import com.example.ui.theme.SalimTextSecondary

object ConfidenceCalculator {

    data class ConfidenceBadgeInfo(
        val label: String,
        val textColor: Color,
        val containerColor: Color,
        val isVerified: Boolean
    )

    fun getBadgeInfo(result: CallerIdentityResult): ConfidenceBadgeInfo {
        return when {
            result.isVerified && result.sourceType == LookupSourceType.DEVICE_CONTACTS -> {
                ConfidenceBadgeInfo(
                    label = "Saved Contact",
                    textColor = SalimGreen,
                    containerColor = Color(0xFFEBF9EE),
                    isVerified = true
                )
            }
            result.isVerified && result.sourceType == LookupSourceType.PUBLIC_BUSINESS_DIRECTORY -> {
                ConfidenceBadgeInfo(
                    label = "Verified Business",
                    textColor = SalimGreen,
                    containerColor = Color(0xFFEBF9EE),
                    isVerified = true
                )
            }
            result.sourceType == LookupSourceType.LOCAL_USER_DIRECTORY -> {
                ConfidenceBadgeInfo(
                    label = "Saved by You",
                    textColor = SalimBlue,
                    containerColor = Color(0xFFEBF4FF),
                    isVerified = false
                )
            }
            result.sourceType == LookupSourceType.COMMUNITY_DIRECTORY -> {
                ConfidenceBadgeInfo(
                    label = "Community Submitted",
                    textColor = SalimOrange,
                    containerColor = Color(0xFFFFF4E5),
                    isVerified = false
                )
            }
            result.sourceType == LookupSourceType.LICENSED_PROVIDER -> {
                ConfidenceBadgeInfo(
                    label = "External Directory",
                    textColor = SalimBlue,
                    containerColor = Color(0xFFEBF4FF),
                    isVerified = result.isVerified
                )
            }
            else -> {
                ConfidenceBadgeInfo(
                    label = "Unverified",
                    textColor = SalimTextSecondary,
                    containerColor = Color(0xFFF2F4F7),
                    isVerified = false
                )
            }
        }
    }
}
