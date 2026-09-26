package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.LookupHistoryEntity
import com.example.data.local.entities.ReportEntity
import com.example.data.local.entities.SavedNumberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalimDao {

    // --- Saved Numbers ---
    @Query("SELECT * FROM saved_numbers ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllSavedNumbers(): Flow<List<SavedNumberEntity>>

    @Query("SELECT * FROM saved_numbers WHERE normalizedNumber = :normalizedNumber LIMIT 1")
    suspend fun findSavedNumber(normalizedNumber: String): SavedNumberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSavedNumber(entry: SavedNumberEntity): Long

    @Update
    suspend fun updateSavedNumber(entry: SavedNumberEntity)

    @Delete
    suspend fun deleteSavedNumber(entry: SavedNumberEntity)

    @Query("DELETE FROM saved_numbers WHERE normalizedNumber = :normalizedNumber")
    suspend fun deleteSavedNumberByNumber(normalizedNumber: String)

    @Query("DELETE FROM saved_numbers")
    suspend fun clearAllSavedNumbers()

    // --- Lookup History ---
    @Query("SELECT * FROM lookup_history ORDER BY lookedUpAt DESC")
    fun getLookupHistory(): Flow<List<LookupHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLookupHistory(history: LookupHistoryEntity): Long

    @Query("DELETE FROM lookup_history WHERE id = :id")
    suspend fun deleteLookupHistoryItem(id: Long)

    @Query("DELETE FROM lookup_history")
    suspend fun clearLookupHistory()

    @Query("DELETE FROM lookup_history WHERE lookedUpAt < :cutoffTimestamp")
    suspend fun deleteHistoryOlderThan(cutoffTimestamp: Long)

    // --- Reports ---
    @Query("SELECT * FROM lookup_reports ORDER BY reportedAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("DELETE FROM lookup_reports")
    suspend fun clearAllReports()
}
