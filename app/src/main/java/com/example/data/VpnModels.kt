package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey val id: Int,
    val countryName: String,
    val countryCode: String,
    val cityName: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercent: Int,
    val isPremium: Boolean,
    val isFavorite: Boolean = false,
    val category: String = "General", // General, Streaming, Gaming, Torrent
    val flagEmoji: String
)

@Entity(tableName = "connection_logs")
data class ConnectionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serverName: String,
    val serverCountry: String,
    val connectedAt: String,
    val durationFormatted: String,
    val dataUsedMb: Double,
    val status: String
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val autoConnect: Boolean = true,
    val autoReconnect: Boolean = true,
    val killSwitch: Boolean = true,
    val protocol: String = "WireGuard", // WireGuard, OpenVPN UDP, OpenVPN TCP, IKEv2, Stealth
    val dnsSelection: String = "Automatic",
    val darkTheme: Boolean = true,
    val language: String = "English",
    val isPremiumUser: Boolean = true,
    val userName: String = "Alex Thompson",
    val userEmail: String = "alex.t@privacy.com",
    val adBlock: Boolean = true,
    val malwareBlock: Boolean = true,
    val splitTunneling: Boolean = false
)
