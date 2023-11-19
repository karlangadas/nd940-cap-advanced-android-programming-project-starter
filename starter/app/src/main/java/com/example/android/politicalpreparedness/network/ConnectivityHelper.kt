package com.example.android.politicalpreparedness.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.util.Log
import timber.log.Timber

// https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(TRANSPORT_CELLULAR)) {
            Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(TRANSPORT_WIFI)) {
            Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(TRANSPORT_ETHERNET)) {
            Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    return false
}