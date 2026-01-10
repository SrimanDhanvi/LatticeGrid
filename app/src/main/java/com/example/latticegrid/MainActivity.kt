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
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
    var statusText by remember() { mutableStateOf("") }
    var downStatisText by remember { mutableStateOf("") }

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
            Text(
                "Battery Percentage",
                Modifier
                    .padding(12.dp),
                fontSize = 20.sp
            )
            Text(
                getBatteryLevel(context).toString(),
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
                        scope.launch(Dispatchers.IO) {
                            val result = downloadLocally(context)
                            withContext(Dispatchers.Main) {
                                downStatisText = result
                            }
                        }
                },
                modifier = Modifier
                    .padding(6.dp)
            ) { Text(
                "Download",
                Modifier
                    .padding(12.dp)
            )}

            Text(
                text = "Download Status: $downStatisText",
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

    val dir = context.filesDir
    File(dir, "sample.txt").writeText("Hello from LatticeGrid")

    server = LocalServer(dir)
    server.start()

    return HostingResult.Started
}

fun downloadLocally(context: Context): String {

    val ip = getGatewayIp(context)
        ?: return "Failed: Not connected to hotspot"

    val outputFile = File(
        context.filesDir,
        "received_from_latticegrid.txt"
    )

    return try {
        downloadFile(
            serverIp = ip,
            outputFile = outputFile
        )
        "Successfully Downloaded"
    } catch (e: Exception) {
        "Download Failed: ${e.message}"
    }
}


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



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LatticeGridTheme {
        MainUI()
    }
}