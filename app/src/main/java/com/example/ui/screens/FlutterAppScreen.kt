package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeViewerCard
import com.example.ui.theme.SleekConnectedGreen
import com.example.ui.theme.SleekDisconnectedRed
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.util.FlutterCodeTemplates
import kotlinx.coroutines.delay

enum class FlutterVpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

@Composable
fun FlutterAppScreen(
    modifier: Modifier = Modifier
) {
    var vpnStatus by remember { mutableStateOf(FlutterVpnStatus.DISCONNECTED) }
    var apiUrl by remember { mutableStateOf("https://vpn-vps-controller.onrender.com/get-vps") }
    var uptimeSeconds by remember { mutableIntStateOf(0) }

    var selectedTab by remember { mutableStateOf(0) }

    // Pulse animation for connecting state
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(vpnStatus) {
        if (vpnStatus == FlutterVpnStatus.CONNECTING) {
            scaleAnim.animateTo(
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            scaleAnim.snapTo(1f)
        }
    }

    // Uptime timer
    LaunchedEffect(vpnStatus) {
        if (vpnStatus == FlutterVpnStatus.CONNECTED) {
            uptimeSeconds = 0
            while (true) {
                delay(1000)
                uptimeSeconds++
            }
        }
    }

    val buttonBgColor by animateColorAsState(
        targetValue = when (vpnStatus) {
            FlutterVpnStatus.CONNECTED -> SleekDisconnectedRed
            FlutterVpnStatus.CONNECTING -> SleekPrimary
            FlutterVpnStatus.DISCONNECTED -> SleekPrimary
        },
        label = "btnBg"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PhoneIphone,
                contentDescription = null,
                modifier = Modifier.width(28.dp),
                tint = SleekPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Flutter Mobile Client App",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Single-file lib/main.dart with Centered Connect/Disconnect button",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("App Interactive Preview") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("lib/main.dart") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("pubspec.yaml") }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("build-apk.yml (GitHub Actions)") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Phone Frame Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flutter_phone_frame"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF7F9FF)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Personal WireGuard VPN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = apiUrl,
                            onValueChange = { apiUrl = it },
                            label = { Text("Backend Controller API URL") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("flutter_api_url_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Status Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(SleekPrimaryContainer)
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (vpnStatus) {
                                            FlutterVpnStatus.CONNECTED -> Icons.Default.CheckCircle
                                            FlutterVpnStatus.CONNECTING -> Icons.Default.Shield
                                            FlutterVpnStatus.DISCONNECTED -> Icons.Default.VpnLock
                                        },
                                        contentDescription = null,
                                        tint = when (vpnStatus) {
                                            FlutterVpnStatus.CONNECTED -> SleekConnectedGreen
                                            FlutterVpnStatus.CONNECTING -> SleekPrimary
                                            FlutterVpnStatus.DISCONNECTED -> Color(0xFF50606E)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (vpnStatus) {
                                            FlutterVpnStatus.CONNECTED -> "Connected"
                                            FlutterVpnStatus.CONNECTING -> "Connecting..."
                                            FlutterVpnStatus.DISCONNECTED -> "Disconnected"
                                        },
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (vpnStatus) {
                                            FlutterVpnStatus.CONNECTED -> SleekConnectedGreen
                                            FlutterVpnStatus.CONNECTING -> SleekPrimary
                                            FlutterVpnStatus.DISCONNECTED -> Color(0xFF50606E)
                                        }
                                    )
                                }

                                if (vpnStatus == FlutterVpnStatus.CONNECTED) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "VPS 1: Oracle Cloud (Singapore) • 139.59.22.10:51820",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF001E2F)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = Color(0xFFAAC7FF))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "UPTIME",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekPrimary
                                            )
                                            val m = (uptimeSeconds % 3600) / 60
                                            val s = uptimeSeconds % 60
                                            Text(
                                                text = "${m}m ${s}s",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "LATENCY",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekPrimary
                                            )
                                            Text(
                                                text = "24 ms",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = SleekConnectedGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        // Large Centered Connect / Disconnect Button
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(170.dp)
                                    .scale(scaleAnim.value)
                                    .clip(CircleShape)
                                    .background(buttonBgColor)
                                    .clickable {
                                        when (vpnStatus) {
                                            FlutterVpnStatus.DISCONNECTED -> {
                                                vpnStatus = FlutterVpnStatus.CONNECTING
                                            }

                                            FlutterVpnStatus.CONNECTING -> {
                                                vpnStatus = FlutterVpnStatus.CONNECTED
                                            }

                                            FlutterVpnStatus.CONNECTED -> {
                                                vpnStatus = FlutterVpnStatus.DISCONNECTED
                                            }
                                        }
                                    }
                                    .testTag("flutter_connect_toggle_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (vpnStatus == FlutterVpnStatus.CONNECTED) Icons.Default.PowerSettingsNew else Icons.Default.VpnLock,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(52.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when (vpnStatus) {
                                            FlutterVpnStatus.CONNECTED -> "DISCONNECT"
                                            FlutterVpnStatus.CONNECTING -> "CONNECTING"
                                            FlutterVpnStatus.DISCONNECTED -> "CONNECT"
                                        },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Tap the large circle to simulate Flutter WireGuard connection sequence.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            1 -> CodeViewerCard(
                title = "lib/main.dart (Flutter Single File App)",
                code = FlutterCodeTemplates.mainDartCode,
                language = "dart"
            )

            2 -> CodeViewerCard(
                title = "pubspec.yaml (Flutter Dependencies)",
                code = FlutterCodeTemplates.pubspecYamlCode,
                language = "yaml"
            )

            3 -> CodeViewerCard(
                title = ".github/workflows/build-apk.yml (GitHub Actions)",
                code = FlutterCodeTemplates.githubWorkflowYaml,
                language = "yaml"
            )
        }
    }
}
