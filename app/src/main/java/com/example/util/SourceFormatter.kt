package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SourceFormatter {

    fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return "Unknown"
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000L -> "Just now"
            diff < 3600_000L -> "${diff / 60_000L}m ago"
            diff < 86400_000L -> "${diff / 3600_000L}h ago"
            diff < 86400_000L * 7 -> "${diff / 86400_000L}d ago"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    fun formatDetailedDate(timestamp: Long): String {
        if (timestamp <= 0) return "Never"
        return SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
