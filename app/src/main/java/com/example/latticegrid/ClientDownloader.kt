package com.example.latticegrid

import java.io.File
import java.net.HttpURLConnection
import java.net.URL

fun downloadFile(
    serverIp: String,
    outputFile: File
) {
    val url = URL("http://$serverIp:8080/file")
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()

    connection.inputStream.use { input ->
        outputFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    connection.disconnect()
}
