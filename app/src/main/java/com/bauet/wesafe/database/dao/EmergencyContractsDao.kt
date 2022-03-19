package com.bauet.wesafe.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContractsDao {
    @Query("SELECT mobileNo FROM EMERGENCY_TABLE")
    fun getAllContracts(): List<String>

    @Query("INSERT INTO EMERGENCY_TABLE (mobileNo) VALUES (:mobileNo)")
    fun insertData(mobileNo: String)

    @Query("DELETE FROM EMERGENCY_TABLE WHERE  mobileNo = :mobileNo")
    fun deleteMobileNumber(mobileNo: String)

}