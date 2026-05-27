package com.example.latticegrid

import android.content.Context
import android.net.wifi.WifiManager

    fun getGatewayIp(context: Context): String? {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager

        val dhcpInfo = wifiManager.dhcpInfo
        return dhcpInfo?.gateway?.let { intToIp(it) }
    }

    fun intToIp(ip: Int): String {
        return listOf(
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        ).joinToString(".")
    }