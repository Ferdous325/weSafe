package com.bauet.wesafe.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit

object SessionManager {
    private val Is_LOGIN = "isLogIn"
    private val Pref_Name = "com.bauet.wesafe.utils"
    private lateinit var pref: SharedPreferences

    fun init(@NonNull context: Context) {
        pref = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE)
    }

    var isLogin: Boolean
        get() {
            return pref.getBoolean(Is_LOGIN, false)
        }
        set(value) {
            pref.edit {
                putBoolean(Is_LOGIN, value)
            }
        }

    var mobile: String
        get() {
            return pref.getString("mobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("mobile", value)
            }
        }
    var password: String
        get() {
            return pref.getString("password", "")!!
        }
        set(value) {
            pref.edit {
                putString("password", value)
            }
        }
}