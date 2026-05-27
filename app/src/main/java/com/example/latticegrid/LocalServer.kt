package com.example.latticegrid

import android.content.Context
import fi.iki.elonen.NanoHTTPD

class LocalServer (
    private val context: Context
) : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession): Response {

        return when (session.uri) {

            "/info" -> {
                newFixedLengthResponse("LatticeGrid Compute Node Active")
            }

            "/task" -> {
                if (session.method != Method.GET) {
                    return newFixedLengthResponse(
                        Response.Status.METHOD_NOT_ALLOWED,
                        MIME_PLAINTEXT,
                        "Only GET supported"
                    )
                }

                val sizeParam = session.parameters["size"]?.firstOrNull()

                if (sizeParam == null) {
                    return newFixedLengthResponse(
                        Response.Status.BAD_REQUEST,
                        MIME_PLAINTEXT,
                        "Missing matrix size"
                    )
                }

                val size = sizeParam.toIntOrNull()
                    ?: return newFixedLengthResponse(
                        Response.Status.BAD_REQUEST,
                        MIME_PLAINTEXT,
                        "Invalid matrix size"
                    )

                // Run computation
                val benchmark = MatrixBenchmark()
                val result = benchmark.runBenchmark(size)

                return newFixedLengthResponse(
                    Response.Status.OK,
                    MIME_PLAINTEXT,
                    result.executionTimeMs.toString()
                )
            }

            "/profile" -> {

                val prefs = context.getSharedPreferences("device_profile", Context.MODE_PRIVATE)
                val avg = prefs.getLong("avg_time_800", -1L)

                if (avg == -1L) {
                    return newFixedLengthResponse(
                        Response.Status.INTERNAL_ERROR,
                        MIME_PLAINTEXT,
                        "Profile not ready"
                    )
                }

                return newFixedLengthResponse(
                    Response.Status.OK,
                    MIME_PLAINTEXT,
                    avg.toString()
                )
            }


            else -> newFixedLengthResponse("Invalid endpoint")
        }
    }
}
