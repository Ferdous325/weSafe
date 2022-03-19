package com.bauet.wesafe.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface HospitalAndOthersDao {
    @Query("SELECT mobileNo FROM hospital_table WHERE uid = :id")
    fun getNumberById(id: Int): String

    @Query("SELECT mobileNo FROM hospital_table")
    fun getAllHelpLineNumbersInfo(): List<String>

    @Query("UPDATE hospital_table SET mobileNo = :mobileNo WHERE uid= :id")
    fun updateMobileNumber(mobileNo: String, id: Int)


    @Query("INSERT INTO hospital_table (mobileNo) VALUES (:mobileNo) ")
    fun insertMobileNumber(mobileNo: String)

}