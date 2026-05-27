package com.example.latticegrid

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun offload(serverIp: String): String =
    withContext(Dispatchers.IO) {

        val start = System.currentTimeMillis()

        val url = URL("http://$serverIp:8080/task?size=800")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val remoteComputeTime =
            connection.inputStream.bufferedReader().readText()

        val end = System.currentTimeMillis()
        val roundTripTime = end - start

        connection.disconnect()

        """
            Matrix Size: 800
            Remote Compute: $remoteComputeTime ms
            Round Trip: $roundTripTime ms
            Network Overhead: ${roundTripTime - remoteComputeTime.toLong()} ms
        """.trimIndent()
    }

