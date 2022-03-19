package com.bauet.wesafe.ui.login

import android.Manifest
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import com.bauet.wesafe.R
import com.bauet.wesafe.ui.home.FacadeViewModel
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

}