package com.bauet.wesafe.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class GpsUtils(private val context: Context) {

    private val mSettingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private val mLocationSettingsRequest: LocationSettingsRequest
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationRequest: LocationRequest = LocationRequest.create()

    init {
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 60 * 1000L
        locationRequest.fastestInterval = locationRequest.interval / 2
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        mLocationSettingsRequest = builder.build()
        builder.setAlwaysShow(true)
    }

    fun turnGPSOn(onGpsListener: ((isGPSEnable: Boolean) -> Unit)? = null) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener?.invoke(true)
        } else {
            mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((context as Activity)) {
                    onGpsListener?.invoke(true)
                }.addOnFailureListener(context) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(context, AppConstant.GPS_REQUEST)
                    } catch (sie: SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate and cannot be fixed here. Fix in Settings."
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}