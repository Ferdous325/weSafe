package com.bauet.wesafe.ui.home

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bauet.wesafe.R
import com.bauet.wesafe.databinding.ActivityHomeBinding
import com.bauet.wesafe.ui.home.fragments.AddContractsFragment
import com.bauet.wesafe.ui.home.fragments.CallHelplineNumbersFragment
import com.bauet.wesafe.ui.login.LoginActivity
import com.bauet.wesafe.utils.SessionManager
import com.bauet.wesafe.utils.toast
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    private var binding: ActivityHomeBinding? = null
    private val viewModel: FacadeViewModel by inject()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    var x = ""
    var y: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initClickListener()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getTheLocation()
        requestPermissions(this, arrayOf(SEND_SMS), 1);
        requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 2);
        requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION), 3);
        requestPermissions(this, arrayOf(CALL_PHONE), 4);
        requestPermissions(this, arrayOf(READ_PHONE_STATE), 5);
        requestPermissions(this, arrayOf(ACCESS_NETWORK_STATE), 6);
        requestPermissions(this, arrayOf(INTERNET), 7);
    }

    fun getTheLocation() {
        locationRequest = LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    currentLocation = it
                    x = currentLocation?.latitude.toString()
                    y = currentLocation?.longitude.toString()
                }
            }
        }
    }


    private fun initClickListener() {
        binding?.addContracts?.setOnClickListener {
            this.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, AddContractsFragment(), "frag1")
                .addToBackStack(null)
                .commit()
        }
        binding?.callHelpline?.setOnClickListener {
            this.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, CallHelplineNumbersFragment(), "frag2")
                .addToBackStack(null)
                .commit()
        }
        binding?.emergency?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                    1);
            } else {
                requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                    1);
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper())
            if (x.isNotEmpty() && y.isNotEmpty()) {
                val msg = "Help Me My Location is : http://maps.google.com/maps?saddr=$x,$y"
                viewModel.getAllContracts().observe(this, Observer { list ->
                    if (list.isNotEmpty()){
                        list.forEach { num -> sendSms(num, msg) }
                    }
                })
                this.toast("Message Sent Successfully")
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            } else {
                getTheLocation()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_Menu) {
            SessionManager.isLogin = false
            startActivity(Intent(this, LoginActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendSms(number: String, msg: String) {
        SmsManager.getDefault().sendTextMessage(number, null, msg, null, null);
    }

}