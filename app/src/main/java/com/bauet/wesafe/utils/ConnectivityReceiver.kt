package com.bauet.wesafe.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isOnline(context))
        }
    }

    private fun isOnline(context: Context?): Boolean {
        var connected = false
        context?.let {
            val cm = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = cm.activeNetwork ?: return false
                val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
                actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val netInfo = cm.activeNetworkInfo
                netInfo?.isConnectedOrConnecting == true
            }
        }
        return connected
    }

}