package com.example.data.repository

import com.example.data.local.SalimDao
import com.example.data.local.entities.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportsRepository(private val salimDao: SalimDao) {

    val allReports: Flow<List<ReportEntity>> = salimDao.getAllReports()

    suspend fun submitReport(
        normalizedNumber: String,
        reportedName: String?,
        reportType: String,
        reasonDescription: String
    ): Long {
        val entity = ReportEntity(
            normalizedNumber = normalizedNumber,
            reportedName = reportedName,
            reportType = reportType,
            reasonDescription = reasonDescription,
            reportedAt = System.currentTimeMillis(),
            status = "PENDING_REVIEW"
        )
        return salimDao.insertReport(entity)
    }

    suspend fun clearReports() {
        salimDao.clearAllReports()
    }
}
