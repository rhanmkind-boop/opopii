package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnServerDao {
    @Query("SELECT * FROM vpn_servers ORDER BY isFavorite DESC, pingMs ASC")
    fun getAllServers(): Flow<List<VpnServer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServer>)

    @Update
    suspend fun updateServer(server: VpnServer)

    @Query("UPDATE vpn_servers SET isFavorite = :isFavorite WHERE id = :serverId")
    suspend fun setFavorite(serverId: Int, isFavorite: Boolean)
}

@Dao
interface ConnectionLogDao {
    @Query("SELECT * FROM connection_logs ORDER BY id DESC LIMIT 20")
    fun getRecentLogs(): Flow<List<ConnectionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConnectionLog)

    @Query("DELETE FROM connection_logs")
    suspend fun clearLogs()
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettings)
}
