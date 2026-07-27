package com.example.data

import kotlinx.coroutines.flow.Flow

class VpnRepository(
    private val serverDao: VpnServerDao,
    private val logDao: ConnectionLogDao,
    private val settingsDao: UserSettingsDao
) {
    val allServers: Flow<List<VpnServer>> = serverDao.getAllServers()
    val recentLogs: Flow<List<ConnectionLog>> = logDao.getRecentLogs()
    val userSettings: Flow<UserSettings?> = settingsDao.getUserSettings()

    suspend fun toggleFavorite(serverId: Int, currentFavoriteState: Boolean) {
        serverDao.setFavorite(serverId, !currentFavoriteState)
    }

    suspend fun addLog(log: ConnectionLog) {
        logDao.insertLog(log)
    }

    suspend fun updateSettings(settings: UserSettings) {
        settingsDao.saveUserSettings(settings)
    }
}
