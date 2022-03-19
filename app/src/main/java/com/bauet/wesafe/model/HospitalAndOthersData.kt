package com.bauet.wesafe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "hospital_table")
data class HospitalAndOthersData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int = 0,

    @ColumnInfo(name = "mobileNo")
    var mobileNo: String = "",
)