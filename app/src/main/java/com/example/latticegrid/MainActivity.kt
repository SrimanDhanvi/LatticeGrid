package com.example.latticegrid

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.latticegrid.ui.theme.LatticeGridTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LatticeGridTheme {
                MainUI()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("") }
    var resultNum by remember { mutableLongStateOf(0L) }
    var offloadStatusText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("LatticeGrid")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

//            Button (
//                onClick = {
//                    CoroutineScope(Dispatchers.Default).launch {
//
//                        val benchmark = MatrixBenchmark()
//                        val result = benchmark.runBenchmark(800)
//
//                        withContext(Dispatchers.Main) {
//                            resultText =
//                                "Matrix Size: ${result.matrixSize}\nTime: ${result.executionTimeMs} ms"
//                        }
//                    }

//                },
//                modifier = Modifier
//                    .padding(6.dp)
//            ) {
//                Text(
//                    "Execute Program",
//                    Modifier
//                        .padding(12.dp)
//                )
//            }

            LaunchedEffect(Unit) {
                scope.launch {
                    val profiler = DeviceProfiler(context)
                    resultNum = profiler.getOrCreateProfile()
                }
            }

            Text(
                "Device Power:",
                Modifier
                    .padding(12.dp),
                fontSize = 20.sp
            )
            Text(
                "$resultNum",
                Modifier
                    .padding(12.dp),
                fontSize = 30.sp
            )

            Button(
                onClick = {
                    when (val result = startHosting(context)) {
                        HostingResult.Started -> {
                            statusText = "Hosting started"
                        }
                        HostingResult.BatteryTooLow -> {
                            statusText = "Battery too low to host"
                        }
                        HostingResult.HotspotDisabled -> {
                            statusText = "Please enable hotspot"
                        }
                        is HostingResult.Error -> {
                            statusText = result.message
                        }
                    }
                },
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Text(
                    "Start Hosting",
                    Modifier
                        .padding(12.dp)
                )
            }
            Text(
                text = "Hosting Status: $statusText",
                Modifier
                    .padding(12.dp)
            )

            val scope = rememberCoroutineScope()

            Button(
                onClick = {
                    scope.launch {
                        offloadStatusText = offloadLocally(context, resultNum)
                    }
                },
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Text(
                    "Start Task",
                    Modifier
                        .padding(12.dp)
                )
            }

            Text(
                text = "Task Status: $offloadStatusText",
                Modifier
                    .padding(12.dp)
            )
        }
    }
}

lateinit var server: LocalServer

sealed class HostingResult {
    object Started : HostingResult()
    object BatteryTooLow : HostingResult()
    object HotspotDisabled : HostingResult()
    data class Error(val message: String) : HostingResult()
}


fun startHosting(context: Context): HostingResult {

    val battery = getBatteryLevel(context)
    if (battery < 40) {
        // do NOT host
        return HostingResult.BatteryTooLow
    }

    if (!isHotspotEnabled(context)) {
        // ask user to enable hotspot
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        return HostingResult.HotspotDisabled
    }

    server = LocalServer(context)
    server.start()

    return HostingResult.Started
}

suspend fun offloadLocally(context: Context, localTime : Long): String {

    val profiler = DeviceProfiler(context)

    val ip = getGatewayIp(context)
        ?: return "Failed: Not connected to hotspot"

    val (remoteTime,networkOverhead) = profiler.fetchRemoteProfile(ip)

    val predictedOffload = remoteTime + networkOverhead

    if (localTime <= predictedOffload) {
        val result = runHere(context)
        return """
            Local Device Has More power.
            Ran Locally.
            $result
        """.trimIndent()
    }

    return try {
        var result : String = offload(ip)
        result
    } catch (e: Exception) {
        "Offload Failed: ${e.message}"
    }
}

fun runHere(context: Context): String {
        val benchmark = MatrixBenchmark()
        val result = benchmark.runBenchmark(800)

        return "Matrix Size: ${result.matrixSize}\nTime: ${result.executionTimeMs} ms"
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LatticeGridTheme {
        MainUI()
    }
}