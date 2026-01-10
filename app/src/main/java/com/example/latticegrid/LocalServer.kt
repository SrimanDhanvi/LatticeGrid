package com.example.latticegrid

import fi.iki.elonen.NanoHTTPD
import java.io.File

class LocalServer(private val sharedDir: File) : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/info" -> {
                newFixedLengthResponse(
                    "LatticeGrid Server Active"
                )
            }

            "/file" -> {
                val file = File(sharedDir, "sample.txt")
                if (!file.exists()) {
                    return newFixedLengthResponse(
                        Response.Status.NOT_FOUND,
                        MIME_PLAINTEXT,
                        "File not found"
                    )
                }
                newChunkedResponse(
                    Response.Status.OK,
                    "application/octet-stream",
                    file.inputStream()
                )
            }

            else -> newFixedLengthResponse("Invalid endpoint")
        }
    }
}
