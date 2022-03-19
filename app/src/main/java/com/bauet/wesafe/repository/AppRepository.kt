package com.bauet.wesafe.repository

import com.bauet.wesafe.database.AppDatabase
import com.bauet.wesafe.database.dao.EmergencyContractsDao
import com.bauet.wesafe.database.dao.HospitalAndOthersDao

class AppRepository(private val database: AppDatabase) {
    private val emergencyContractsDao: EmergencyContractsDao = database.emergencyContractsDao()
    private val hospitalAndOthersData: HospitalAndOthersDao = database.hospitalAndOthersDao()

    suspend fun getAllContracts() =
        emergencyContractsDao.getAllContracts()

    suspend fun insertData(mobileNo: String) =
        emergencyContractsDao.insertData(mobileNo)

    suspend fun deleteMobileNumber(mobileNo: String) =
        emergencyContractsDao.deleteMobileNumber(mobileNo)

    suspend fun getNumberById(id: Int) =
        hospitalAndOthersData.getNumberById(id)

    suspend fun getAllHelpLineNumbersInfo() =
        hospitalAndOthersData.getAllHelpLineNumbersInfo()

    suspend fun updateMobileNumber(mobileNo: String, id: Int) =
        hospitalAndOthersData.updateMobileNumber(mobileNo, id)

    suspend fun insertMobileNumber(mobileNo: String) =
        hospitalAndOthersData.insertMobileNumber(mobileNo)
}