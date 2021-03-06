package com.bauet.wesafe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bauet.wesafe.database.dao.EmergencyContractsDao
import com.bauet.wesafe.database.dao.HospitalAndOthersDao
import com.bauet.wesafe.model.EmergencyContractsData
import com.bauet.wesafe.model.HospitalAndOthersData

@Database(entities = [HospitalAndOthersData::class, EmergencyContractsData::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyContractsDao(): EmergencyContractsDao
    abstract fun hospitalAndOthersDao(): HospitalAndOthersDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "weSafe_db"
        )
            .allowMainThreadQueries()
            .build()
    }


}