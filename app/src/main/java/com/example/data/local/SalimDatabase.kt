package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.entities.LookupHistoryEntity
import com.example.data.local.entities.ReportEntity
import com.example.data.local.entities.SavedNumberEntity

@Database(
    entities = [
        SavedNumberEntity::class,
        LookupHistoryEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SalimDatabase : RoomDatabase() {

    abstract fun salimDao(): SalimDao

    companion object {
        @Volatile
        private var INSTANCE: SalimDatabase? = null

        fun getDatabase(context: Context): SalimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalimDatabase::class.java,
                    "salim_identity_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
