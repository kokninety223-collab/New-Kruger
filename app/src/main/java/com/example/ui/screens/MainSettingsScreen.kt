package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.VpsViewModel
import com.example.ui.theme.MGuardBackground
import com.example.ui.theme.MGuardBorder
import com.example.ui.theme.MGuardGreen
import com.example.ui.theme.MGuardIconCircleBg
import com.example.ui.theme.MGuardSurface
import com.example.ui.theme.MGuardTextPrimary
import com.example.ui.theme.MGuardTextSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun MainSettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    selectedServerName: String,
    onServerSelect: (String) -> Unit,
    viewModel: VpsViewModel,
    onOpenDeveloperTools: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }
    var autoRotate by remember { mutableStateOf(true) }
    var apiUrlInput by remember { mutableStateOf("https://vpn-vps-controller.onrender.com/get-vps") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MGuardBackground)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = strings.tabSettings,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MGuardTextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Language Section
        Text(
            text = strings.languageSetting,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MGuardTextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                AppLanguage.entries.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageChange(lang) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = if (currentLanguage == lang) MGuardGreen else MGuardTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = lang.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MGuardTextPrimary
                            )
                        }

                        RadioButton(
                            selected = currentLanguage == lang,
                            onClick = { onLanguageChange(lang) },
                            colors = RadioButtonDefaults.colors(selectedColor = MGuardGreen)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Available Servers Section
        Text(
            text = strings.selectServer,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MGuardTextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.autoRotate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MGuardTextPrimary
                    )
                    Switch(
                        checked = autoRotate,
                        onCheckedChange = { autoRotate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MGuardGreen,
                            checkedTrackColor = Color(0xFF0D3321)
                        )
                    )
                }

                Divider(color = MGuardBorder, modifier = Modifier.padding(vertical = 12.dp))

                val servers = listOf(
                    Triple("VPS 1 (Primary): Oracle Cloud Free (Singapore)", strings.vps1Desc, "18ms • Active Primary"),
                    Triple("VPS 2 (Secondary): AWS Free Tier (Tokyo)", strings.vps2Desc, "42ms • Fallback 1"),
                    Triple("VPS 3 (Backup): Google Cloud Free (Taiwan)", strings.vps3Desc, "55ms • Fallback 2")
                )

                servers.forEach { (serverTitle, fallbackDesc, pingDetails) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onServerSelect(serverTitle) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedServerName == serverTitle) Color(0xFF0D3321) else MGuardIconCircleBg
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = null,
                                    tint = if (selectedServerName == serverTitle) MGuardGreen else MGuardTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = serverTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MGuardTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = fallbackDesc,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedServerName == serverTitle) MGuardGreen else MGuardTextSecondary
                                )
                                Text(
                                    text = pingDetails,
                                    fontSize = 11.sp,
                                    color = MGuardTextSecondary.copy(alpha = 0.7f)
                                )
                            }
                        }

                        RadioButton(
                            selected = selectedServerName == serverTitle,
                            onClick = { onServerSelect(serverTitle) },
                            colors = RadioButtonDefaults.colors(selectedColor = MGuardGreen)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Backend API Settings
        Text(
            text = strings.backendApiUrl,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MGuardTextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apiUrlInput,
            onValueChange = { apiUrlInput = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MGuardSurface,
                unfocusedContainerColor = MGuardSurface,
                focusedBorderColor = MGuardGreen,
                unfocusedBorderColor = MGuardBorder,
                focusedTextColor = MGuardTextPrimary,
                unfocusedTextColor = MGuardTextPrimary
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Api,
                    contentDescription = null,
                    tint = MGuardGreen
                )
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Developer Tools Expandable Options
        Text(
            text = strings.developerTools,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MGuardTextSecondary,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MGuardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MGuardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // View Flutter Main.dart Code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDeveloperTools(1) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneIphone,
                        contentDescription = null,
                        tint = MGuardGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Flutter Mobile Code (lib/main.dart)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MGuardTextPrimary
                        )
                        Text(
                            text = "View & export Flutter WireGuard client script",
                            fontSize = 12.sp,
                            color = MGuardTextSecondary
                        )
                    }
                }

                Divider(color = MGuardBorder)

                // View FastAPI Python Code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenDeveloperTools(2) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = MGuardGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FastAPI Backend Code (main.py)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MGuardTextPrimary
                        )
                        Text(
                            text = "View Python code for Render / Railway deployment",
                            fontSize = 12.sp,
                            color = MGuardTextSecondary
                        )
                    }
                }
            }
        }
    }
}
