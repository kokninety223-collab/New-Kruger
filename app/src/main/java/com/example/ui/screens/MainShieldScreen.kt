package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.MGuardBackground
import com.example.ui.theme.MGuardBorder
import com.example.ui.theme.MGuardGreen
import com.example.ui.theme.MGuardIconCircleBg
import com.example.ui.theme.MGuardRed
import com.example.ui.theme.MGuardSurface
import com.example.ui.theme.MGuardTextPrimary
import com.example.ui.theme.MGuardTextSecondary
import com.example.ui.theme.MGuardWarning
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.delay

enum class VpnConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

@Composable
fun MainShieldScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    selectedServerName: String,
    onSelectServerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }
    var connectionState by remember { mutableStateOf(VpnConnectionState.DISCONNECTED) }
    var activeSeconds by remember { mutableIntStateOf(0) }

    // Pulsing effect for connecting
    var isPulsing by remember { mutableStateOf(false) }
    val pulseScale by animateFloatAsState(
        targetValue = if (isPulsing) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(connectionState) {
        if (connectionState == VpnConnectionState.CONNECTING) {
            isPulsing = true
            delay(1800) // Simulate WireGuard handshake & endpoint fetch
            connectionState = VpnConnectionState.CONNECTED
            isPulsing = false
        } else {
            isPulsing = false
        }
    }

    // Timer logic
    LaunchedEffect(connectionState) {
        if (connectionState == VpnConnectionState.CONNECTED) {
            activeSeconds = 0
            while (true) {
                delay(1000)
                activeSeconds++
            }
        }
    }

    val buttonColor by animateColorAsState(
        targetValue = when (connectionState) {
            VpnConnectionState.CONNECTED -> MGuardRed
            VpnConnectionState.CONNECTING -> Color(0xFF0288D1)
            VpnConnectionState.DISCONNECTED -> MGuardGreen
        },
        label = "btnColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MGuardBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header: mGuard Native VPN Protection + Language Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = strings.appTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MGuardTextPrimary
                )
                Text(
                    text = strings.appSubtitle,
                    fontSize = 14.sp,
                    color = MGuardTextSecondary
                )
            }

            // Language pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MGuardSurface)
                    .border(1.dp, MGuardBorder, RoundedCornerShape(20.dp))
                    .clickable {
                        val nextLang = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.BURMESE else AppLanguage.ENGLISH
                        onLanguageChange(nextLang)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("language_toggle_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MGuardGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentLanguage.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MGuardTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Shield Card (Matches Ref 2)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("main_shield_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circle Shield Container
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(MGuardIconCircleBg)
                        .border(
                            2.dp,
                            when (connectionState) {
                                VpnConnectionState.CONNECTED -> MGuardGreen
                                VpnConnectionState.CONNECTING -> Color(0xFF0288D1)
                                VpnConnectionState.DISCONNECTED -> MGuardBorder
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (connectionState) {
                            VpnConnectionState.CONNECTED -> Icons.Default.Check
                            VpnConnectionState.CONNECTING -> Icons.Default.Shield
                            VpnConnectionState.DISCONNECTED -> Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = when (connectionState) {
                            VpnConnectionState.CONNECTED -> MGuardGreen
                            VpnConnectionState.CONNECTING -> Color(0xFF0288D1)
                            VpnConnectionState.DISCONNECTED -> MGuardWarning
                        },
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title Status
                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> strings.shieldActive
                        VpnConnectionState.CONNECTING -> strings.shieldConnecting
                        VpnConnectionState.DISCONNECTED -> strings.shieldInactive
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MGuardTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (connectionState) {
                        VpnConnectionState.CONNECTED -> strings.connectionEncrypted
                        VpnConnectionState.CONNECTING -> strings.btnConnecting
                        VpnConnectionState.DISCONNECTED -> strings.tapToSecure
                    },
                    fontSize = 13.sp,
                    color = MGuardTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Secure Connection Green Button
                Button(
                    onClick = {
                        when (connectionState) {
                            VpnConnectionState.DISCONNECTED -> connectionState = VpnConnectionState.CONNECTING
                            VpnConnectionState.CONNECTING -> {}
                            VpnConnectionState.CONNECTED -> connectionState = VpnConnectionState.DISCONNECTED
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("secure_connection_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = if (connectionState == VpnConnectionState.CONNECTED) Color.White else Color(0xFF04140C)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (connectionState) {
                                VpnConnectionState.CONNECTED -> Icons.Default.Stop
                                VpnConnectionState.CONNECTING -> Icons.Default.Shield
                                VpnConnectionState.DISCONNECTED -> Icons.Default.PlayArrow
                            },
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (connectionState) {
                                VpnConnectionState.CONNECTED -> strings.btnDisconnect
                                VpnConnectionState.CONNECTING -> strings.btnConnecting
                                VpnConnectionState.DISCONNECTED -> strings.btnSecureConnection
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Since Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_since_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MGuardIconCircleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (connectionState == VpnConnectionState.CONNECTED) MGuardGreen else MGuardTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = strings.activeSince,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MGuardTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (connectionState == VpnConnectionState.CONNECTED) {
                            val hours = activeSeconds / 3600
                            val mins = (activeSeconds % 3600) / 60
                            val secs = activeSeconds % 60
                            String.format("%02dh %02dm %02ds", hours, mins, secs)
                        } else {
                            strings.notConnected
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MGuardTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Server Location & Latency Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectServerClick() }
                .testTag("selected_server_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MGuardIconCircleBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = null,
                            tint = MGuardGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = strings.selectedServerLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MGuardTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedServerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MGuardTextPrimary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (connectionState == VpnConnectionState.CONNECTED) MGuardGreen else MGuardTextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (connectionState == VpnConnectionState.CONNECTED) "24ms" else "Standby",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (connectionState == VpnConnectionState.CONNECTED) MGuardGreen else MGuardTextSecondary
                    )
                }
            }
        }
    }
}
