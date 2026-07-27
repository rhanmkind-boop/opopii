package com.example.ui.settings

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.UserSettings
import com.example.ui.components.GlassCard
import com.example.ui.theme.PremiumContainerOrange
import com.example.ui.theme.VpnConnectedGreen
import com.example.ui.theme.VpnErrorRed

@Composable
fun SettingsScreen(
    userSettings: UserSettings?,
    onUpdateSettings: (UserSettings) -> Unit,
    onOpenSpeedTest: () -> Unit,
    onOpenAdminPanel: () -> Unit
) {
    val settings = userSettings ?: UserSettings()
    val scrollState = rememberScrollState()

    var showProtocolDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDnsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }

    val protocols = listOf("WireGuard", "OpenVPN UDP", "OpenVPN TCP", "IKEv2", "Stealth Mode")
    val languages = listOf("English", "বাংলা", "हिन्दी", "العربية", "Français", "Deutsch", "Español")
    val dnsList = listOf("Automatic", "Cloudflare (1.1.1.1)", "Google DNS (8.8.8.8)", "AdGuard DNS")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Premium Banner
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("premium_banner"),
            backgroundColor = Color(0x334A8EFF),
            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Pro",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Unlock Pro Speed & Servers",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Get 10Gbps ultra-fast servers, unlimited bandwidth, and ad-free protection with SecureVPN Premium.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onUpdateSettings(settings.copy(isPremiumUser = true))
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (settings.isPremiumUser) "PRO PLAN ACTIVE" else "GO PREMIUM NOW",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Profile Card
        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = settings.userName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = settings.userEmail,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (settings.isPremiumUser) PremiumContainerOrange.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (settings.isPremiumUser) "PREMIUM MEMBER" else "FREE MEMBER",
                            color = if (settings.isPremiumUser) PremiumContainerOrange else MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = { showProfileDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quick Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(
                modifier = Modifier.weight(1f),
                onClick = onOpenSpeedTest
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed Test",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Speed Test",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            GlassCard(
                modifier = Modifier.weight(1f),
                onClick = onOpenAdminPanel
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = Color(0xFF00F4FE),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Panel",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // VPN Settings Section
        SectionHeader("VPN SETTINGS")
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Auto Connect
                SettingToggleItem(
                    icon = Icons.Default.AutoMode,
                    title = "Auto Connect",
                    subtitle = "Automatically connect on launch / untrusted networks",
                    checked = settings.autoConnect,
                    onCheckedChange = { onUpdateSettings(settings.copy(autoConnect = it)) }
                )

                // Protocol
                SettingRowItem(
                    icon = Icons.Default.Lock,
                    title = "Protocol",
                    valueText = settings.protocol,
                    onClick = { showProtocolDialog = true }
                )

                // Kill Switch
                SettingToggleItem(
                    icon = Icons.Default.Emergency,
                    title = "Kill Switch",
                    subtitle = "Block traffic if VPN disconnects unexpectedly",
                    checked = settings.killSwitch,
                    onCheckedChange = { onUpdateSettings(settings.copy(killSwitch = it)) }
                )
            }
        }

        // Security & Network Section
        SectionHeader("NETWORK & PROTECTION")
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Split Tunneling
                SettingToggleItem(
                    icon = Icons.Default.AltRoute,
                    title = "Split Tunneling",
                    subtitle = "Select specific apps to bypass VPN encryption",
                    checked = settings.splitTunneling,
                    onCheckedChange = { onUpdateSettings(settings.copy(splitTunneling = it)) }
                )

                // DNS Selection
                SettingRowItem(
                    icon = Icons.Default.Dns,
                    title = "DNS Selection",
                    valueText = settings.dnsSelection,
                    onClick = { showDnsDialog = true }
                )

                // Ad & Tracker Block
                SettingToggleItem(
                    icon = Icons.Default.Security,
                    title = "Ad & Tracker Blocker",
                    subtitle = "Block malicious ads and domain trackers automatically",
                    checked = settings.adBlock,
                    onCheckedChange = { onUpdateSettings(settings.copy(adBlock = it)) }
                )
            }
        }

        // Appearance & Language
        SectionHeader("APPEARANCE & PREFERENCES")
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingRowItem(
                    icon = Icons.Default.DarkMode,
                    title = "Theme",
                    valueText = if (settings.darkTheme) "Dark Mode" else "Light Mode",
                    onClick = { onUpdateSettings(settings.copy(darkTheme = !settings.darkTheme)) }
                )

                SettingRowItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    valueText = settings.language,
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        // Account & Help
        SectionHeader("ACCOUNT & SUPPORT")
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingRowItem(
                    icon = Icons.Default.LockReset,
                    title = "Change Password",
                    valueText = "",
                    onClick = { showProfileDialog = true }
                )

                SettingRowItem(
                    icon = Icons.Default.SupportAgent,
                    title = "24/7 Live Support Chat",
                    valueText = "Online",
                    onClick = { showChatDialog = true }
                )

                SettingRowItem(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy & Terms",
                    valueText = "",
                    onClick = {}
                )

                SettingRowItem(
                    icon = Icons.Default.Info,
                    title = "About SecureVPN Pro",
                    valueText = "v2.4.0",
                    onClick = {}
                )

                SettingRowItem(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    valueText = "",
                    titleColor = VpnErrorRed,
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Protocol Dialog
    if (showProtocolDialog) {
        AlertDialog(
            onDismissRequest = { showProtocolDialog = false },
            title = { Text("Select VPN Protocol", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    protocols.forEach { proto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings(settings.copy(protocol = proto))
                                    showProtocolDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (proto == settings.protocol),
                                onClick = {
                                    onUpdateSettings(settings.copy(protocol = proto))
                                    showProtocolDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(proto, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProtocolDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select App Language", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings(settings.copy(language = lang))
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (lang == settings.language),
                                onClick = {
                                    onUpdateSettings(settings.copy(language = lang))
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // DNS Selection Dialog
    if (showDnsDialog) {
        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            title = { Text("Select DNS Provider", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    dnsList.forEach { dns ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings(settings.copy(dnsSelection = dns))
                                    showDnsDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (dns == settings.dnsSelection),
                                onClick = {
                                    onUpdateSettings(settings.copy(dnsSelection = dns))
                                    showDnsDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(dns, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDnsDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Edit Profile Dialog
    if (showProfileDialog) {
        var nameInput by remember { mutableStateOf(settings.userName) }
        var emailInput by remember { mutableStateOf(settings.userEmail) }

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Edit User Profile", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSettings(settings.copy(userName = nameInput, userEmail = emailInput))
                        showProfileDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Live Support Chat Dialog
    if (showChatDialog) {
        AlertDialog(
            onDismissRequest = { showChatDialog = false },
            title = { Text("24/7 Priority Support", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Support Agent Sarah is online.", color = VpnConnectedGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Welcome to SecureVPN Pro! How can we assist you with your connection or subscription today?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showChatDialog = false }) {
                    Text("Start Live Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun SettingRowItem(
    icon: ImageVector,
    title: String,
    valueText: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (titleColor == VpnErrorRed) VpnErrorRed else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = titleColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (valueText.isNotEmpty()) {
                Text(
                    text = valueText,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
