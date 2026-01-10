package com.example.latticegrid

import android.content.Context
import android.net.wifi.WifiManager

fun isHotspotEnabled(context: Context): Boolean {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    return try {
        val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
        method.isAccessible = true
        method.invoke(wifiManager) as Boolean
    } catch (e: Exception) {
        false
    }
}
