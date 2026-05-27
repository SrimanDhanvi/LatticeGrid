package com.example.latticegrid

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class DeviceProfiler(private val context: Context) {

    private val prefs =
        context.getSharedPreferences("device_profile", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AVG_TIME_800 = "avg_time_800"
    }

    suspend fun getOrCreateProfile(): Long {

        // Check if already profiled
        val saved = prefs.getLong(KEY_AVG_TIME_800, -1L)
        if (saved != -1L) {
            return saved
        }

        // Otherwise profile now
        val average = runBenchmarkAverage(800, runs = 3)

        // Save it
        prefs.edit()
            .putLong(KEY_AVG_TIME_800, average)
            .apply()

        return average
    }


    suspend fun fetchRemoteProfile(serverIp: String): Pair<Long, Long> =
        withContext(Dispatchers.IO) {

            val start = System.currentTimeMillis()

            val url = URL("http://$serverIp:8080/profile")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val response =
                connection.inputStream.bufferedReader().readText()

            val end = System.currentTimeMillis()
            val roundTripTime = end - start

            connection.disconnect()

            val remoteComputeTime = response.toLong()

            Pair(remoteComputeTime, roundTripTime)
        }

    private suspend fun runBenchmarkAverage(
        size: Int,
        runs: Int
    ): Long = withContext(Dispatchers.Default) {

        var total = 0L

        MatrixBenchmark().runBenchmark(size)

        repeat(runs) {
            val result = MatrixBenchmark().runBenchmark(size)
            total += result.executionTimeMs
        }

        total / runs
    }
}
