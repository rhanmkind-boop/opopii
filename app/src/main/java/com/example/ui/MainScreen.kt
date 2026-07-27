package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.admin.AdminPanelDialog
import com.example.ui.components.SecureVpnTopAppBar
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.home.HomeScreen
import com.example.ui.servers.ServerListScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.speedtest.SpeedTestDialog
import com.example.vpn.VpnViewModel

enum class MainTab {
    HOME,
    SERVERS,
    DASHBOARD,
    SETTINGS
}

@Composable
fun MainScreen(
    viewModel: VpnViewModel
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    val vpnState by viewModel.connectionState.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServer.collectAsStateWithLifecycle()
    val sessionSeconds by viewModel.sessionSeconds.collectAsStateWithLifecycle()
    val downloadMbps by viewModel.downloadSpeed.collectAsStateWithLifecycle()
    val uploadMbps by viewModel.uploadSpeed.collectAsStateWithLifecycle()
    val pingMs by viewModel.currentPing.collectAsStateWithLifecycle()
    val adsBlocked by viewModel.adsBlocked.collectAsStateWithLifecycle()
    val malwareBlocked by viewModel.malwareBlocked.collectAsStateWithLifecycle()
    val dataTodayMb by viewModel.dataTodayMb.collectAsStateWithLifecycle()

    val allServers by viewModel.allServers.collectAsStateWithLifecycle()
    val recentLogs by viewModel.recentLogs.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val speedTestState by viewModel.speedTest.collectAsStateWithLifecycle()

    var showSpeedTestDialog by remember { mutableStateOf(false) }
    var showAdminDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SecureVpnTopAppBar(
                onNotificationClick = { showAdminDialog = true }
            )
        },
        bottomBar = {
            CustomBottomNavigationBar(
                currentTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTab,
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    MainTab.HOME -> HomeScreen(
                        vpnState = vpnState,
                        selectedServer = selectedServer,
                        sessionSeconds = sessionSeconds,
                        downloadMbps = downloadMbps,
                        uploadMbps = uploadMbps,
                        pingMs = pingMs,
                        dataTodayMb = dataTodayMb,
                        onToggleConnect = { viewModel.toggleConnection() },
                        onSelectServerClick = { selectedTab = MainTab.SERVERS },
                        onOpenSpeedTestClick = { showSpeedTestDialog = true }
                    )

                    MainTab.SERVERS -> ServerListScreen(
                        servers = allServers,
                        selectedServer = selectedServer,
                        onServerSelect = { server ->
                            viewModel.selectServer(server)
                            selectedTab = MainTab.HOME
                        },
                        onToggleFavorite = { server ->
                            viewModel.toggleFavorite(server)
                        }
                    )

                    MainTab.DASHBOARD -> DashboardScreen(
                        recentLogs = recentLogs,
                        adsBlockedCount = adsBlocked,
                        malwareBlockedCount = malwareBlocked,
                        dataTodayMb = dataTodayMb
                    )

                    MainTab.SETTINGS -> SettingsScreen(
                        userSettings = userSettings,
                        onUpdateSettings = { newSettings -> viewModel.updateSettings(newSettings) },
                        onOpenSpeedTest = { showSpeedTestDialog = true },
                        onOpenAdminPanel = { showAdminDialog = true }
                    )
                }
            }
        }
    }

    if (showSpeedTestDialog) {
        SpeedTestDialog(
            speedTest = speedTestState,
            onStartTest = { viewModel.runSpeedTest() },
            onDismiss = { showSpeedTestDialog = false }
        )
    }

    if (showAdminDialog) {
        AdminPanelDialog(
            onDismiss = { showAdminDialog = false }
        )
    }
}

@Composable
fun CustomBottomNavigationBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp)
            .background(Color(0xCC1A1F2F))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                title = "Home",
                icon = Icons.Default.Home,
                isSelected = (currentTab == MainTab.HOME),
                onClick = { onTabSelected(MainTab.HOME) },
                testTag = "nav_tab_home"
            )

            NavTabItem(
                title = "Servers",
                icon = Icons.Default.Dns,
                isSelected = (currentTab == MainTab.SERVERS),
                onClick = { onTabSelected(MainTab.SERVERS) },
                testTag = "nav_tab_servers"
            )

            NavTabItem(
                title = "Dashboard",
                icon = Icons.Default.Dashboard,
                isSelected = (currentTab == MainTab.DASHBOARD),
                onClick = { onTabSelected(MainTab.DASHBOARD) },
                testTag = "nav_tab_dashboard"
            )

            NavTabItem(
                title = "Settings",
                icon = Icons.Default.Settings,
                isSelected = (currentTab == MainTab.SETTINGS),
                onClick = { onTabSelected(MainTab.SETTINGS) },
                testTag = "nav_tab_settings"
            )
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
