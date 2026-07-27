package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.VpsViewModel
import com.example.ui.screens.FastApiDeployScreen
import com.example.ui.screens.FlutterAppScreen
import com.example.ui.screens.MainSettingsScreen
import com.example.ui.screens.MainShieldScreen
import com.example.ui.theme.MGuardBackground
import com.example.ui.theme.MGuardBorder
import com.example.ui.theme.MGuardGreen
import com.example.ui.theme.MGuardSurface
import com.example.ui.theme.MGuardTextPrimary
import com.example.ui.theme.MGuardTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.AppStrings

enum class AppMainTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    SHIELD(Icons.Filled.Security, Icons.Outlined.Security, "tab_shield"),
    SETTINGS(Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: VpsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var selectedTab by remember { mutableStateOf(AppMainTab.SHIELD) }
                var currentLanguage by remember { mutableStateOf(AppLanguage.BURMESE) } // Default Burmese
                var selectedServerName by remember { mutableStateOf("VPS 1 (Primary): Oracle Cloud Free (Singapore)") }
                var activeDeveloperSubScreen by remember { mutableIntStateOf(0) } // 0 = none, 1 = Flutter, 2 = FastAPI

                val strings = remember(currentLanguage) { AppStrings(currentLanguage) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MGuardBackground),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                            containerColor = MGuardSurface,
                            contentColor = MGuardTextPrimary
                        ) {
                            AppMainTab.entries.forEach { tab ->
                                val isSelected = selectedTab == tab
                                val labelText = when (tab) {
                                    AppMainTab.SHIELD -> strings.tabShield
                                    AppMainTab.SETTINGS -> strings.tabSettings
                                }

                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTab = tab
                                        activeDeveloperSubScreen = 0
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = labelText,
                                            tint = if (isSelected) MGuardGreen else MGuardTextSecondary
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = labelText,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MGuardGreen else MGuardTextSecondary
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFF1B2638)
                                    ),
                                    modifier = Modifier.testTag(tab.tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    if (activeDeveloperSubScreen == 1) {
                        FlutterAppScreen(modifier = Modifier.padding(innerPadding))
                    } else if (activeDeveloperSubScreen == 2) {
                        FastApiDeployScreen(modifier = Modifier.padding(innerPadding))
                    } else {
                        when (selectedTab) {
                            AppMainTab.SHIELD -> MainShieldScreen(
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it },
                                selectedServerName = selectedServerName,
                                onSelectServerClick = { selectedTab = AppMainTab.SETTINGS },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppMainTab.SETTINGS -> MainSettingsScreen(
                                currentLanguage = currentLanguage,
                                onLanguageChange = { currentLanguage = it },
                                selectedServerName = selectedServerName,
                                onServerSelect = { selectedServerName = it },
                                viewModel = viewModel,
                                onOpenDeveloperTools = { activeDeveloperSubScreen = it },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
