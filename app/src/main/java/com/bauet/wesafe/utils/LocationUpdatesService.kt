package com.bauet.wesafe.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bauet.wesafe.ui.home.HomeActivity
import com.google.android.gms.location.*
import java.text.SimpleDateFormat
import java.util.*
import com.bauet.wesafe.R

class LocationUpdatesService : Service() {

    private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
    private val channelID = "channel1"
    private val NOTIFICATION_ID = 2620

    private val iBinder: IBinder = LocalBinder()
    private var UPDATE_INTERVAL_IN_MILLISECONDS = 10000L
    private var FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    private var changingConfiguration: Boolean = false
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: NotificationCompat.Builder

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var serviceHandler: Handler
    private var currentLocation: Location? = null
    private lateinit var geoCoder: Geocoder
    private var lastAddress: String = "Searching location"

    private var lastLat: Double = 0.0
    private var lastLng: Double = 0.0
    private var distanceDifferenceInMeter = 20.0f

    companion object {
        val PACKAGE_NAME = "com.bd.deliverytiger.app.services.LocationUpdatesService"
        val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"


        @Suppress("DEPRECATION")
        fun isServiceRunningForeground(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationUpdatesService::class.java.name == service.service.className) {
                    return service.foreground
                }
            }
            return false
        }

    }

    override fun onCreate() {
        super.onCreate()
        geoCoder = Geocoder(this, Locale.US)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(LocationResult: LocationResult?) {
                super.onLocationResult(LocationResult)
                onNewLocation(LocationResult?.lastLocation)
            }
        }
        createLocationRequest()
        getLastLocation()

        val handleThread = HandlerThread(LocationUpdatesService::class.java.name)
        handleThread.start()
        serviceHandler = Handler(handleThread.looper)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelID, "Service", NotificationManager.IMPORTANCE_LOW)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceHandler.removeCallbacksAndMessages(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startedFromNotification =
            intent?.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false) ?: false
        if (startedFromNotification) {
            removeLocationUpdates()
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changingConfiguration = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        changingConfiguration = false
        return iBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        changingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!changingConfiguration) {

            val destinationIntent = Intent(this, LocationUpdatesService::class.java).apply {
                putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
            }
            val servicePendingIntent = PendingIntent.getService(this,
                0,
                destinationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            val activityIntent = Intent(this, HomeActivity::class.java)
            val activityPendingIntent = PendingIntent.getActivity(this,
                9620,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            notification = NotificationCompat.Builder(this, channelID)
                .setContentTitle("Current Location")
                .setContentText(lastAddress)
                .setTicker(lastAddress)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.ic_launcher_foreground, "Open App", activityPendingIntent)
            //.addAction(R.mipmap.ic_launcher, "Remove location updates", servicePendingIntent)

            startForeground(NOTIFICATION_ID, notification.build())
        }
        return true
    }

    private fun onNewLocation(location: Location?) {

        this.currentLocation = location
        val intent = Intent(ACTION_BROADCAST).apply {
            putExtra(EXTRA_LOCATION, location)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        //updateLocationOnServer(currentLocation)
        if (isServiceRunningForeground(this)) {

            addressFromLocation(currentLocation)
            notification.setContentText(lastAddress)
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }

    }

    private fun getLastLocation() {
        try {
            fusedLocationClient.lastLocation.addOnCompleteListener() { task ->
                if (task.isSuccessful && task.result != null) {
                    currentLocation = task.result
                    addressFromLocation(currentLocation)
                }
            }
        } catch (e: SecurityException) { }
    }

    private fun addressFromLocation(location: Location?) {
        location?.let {
            try {
                val list = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                if (list.isNotEmpty()) {
                    val addressObj = list.first()
                    lastAddress = addressObj.getAddressLine(0)
                }
            } catch (e: Exception) { }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun recreateLocationRequest() {
        fusedLocationClient.removeLocationUpdates(locationCallback).addOnSuccessListener {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.myLooper())
            } catch (e: SecurityException) { }
        }
    }

    fun requestLocationUpdates() {
        startService(Intent(applicationContext, LocationUpdatesService::class.java))
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper())
        } catch (e: SecurityException) {
        }
    }

    fun removeLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            stopSelf()
        } catch (e: SecurityException) {
        }
    }

    fun setLocationInterval(intervalInMinute: Int) {
        UPDATE_INTERVAL_IN_MILLISECONDS = intervalInMinute * 60 * 1000L
        FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        with(locationRequest) {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        }
    }

    fun setLocationDifference(difference: Int) {
        distanceDifferenceInMeter = difference.toFloat()
    }

    inner class LocalBinder : Binder() {
        fun getServerInstance(): LocationUpdatesService = this@LocationUpdatesService
    }

}