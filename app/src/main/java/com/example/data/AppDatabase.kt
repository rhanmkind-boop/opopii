package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [VpnServer::class, ConnectionLog::class, UserSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vpnServerDao(): VpnServerDao
    abstract fun connectionLogDao(): ConnectionLogDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "secure_vpn_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val serverDao = database.vpnServerDao()
            val logDao = database.connectionLogDao()
            val settingsDao = database.userSettingsDao()

            val defaultServers = listOf(
                VpnServer(1, "United States", "US", "San Francisco, CA", "104.28.14.92", 24, 42, true, true, "General", "🇺🇸"),
                VpnServer(2, "United States", "US", "New York, NY", "198.51.100.12", 12, 18, false, true, "Streaming", "🇺🇸"),
                VpnServer(3, "United Kingdom", "GB", "London", "185.220.101.4", 85, 15, false, false, "General", "🇬🇧"),
                VpnServer(4, "Singapore", "SG", "Singapore City", "139.99.120.45", 14, 22, false, true, "General", "🇸🇬"),
                VpnServer(5, "Germany", "DE", "Frankfurt", "185.233.100.1", 112, 89, true, false, "Torrent", "🇩🇪"),
                VpnServer(6, "France", "FR", "Paris", "51.15.20.100", 95, 30, false, false, "General", "🇫🇷"),
                VpnServer(7, "Japan", "JP", "Tokyo", "103.201.12.5", 210, 12, true, false, "Gaming", "🇯🇵"),
                VpnServer(8, "Canada", "CA", "Toronto", "142.44.200.11", 38, 25, false, false, "General", "🇨🇦"),
                VpnServer(9, "Netherlands", "NL", "Amsterdam", "89.187.160.2", 72, 45, false, false, "Torrent", "🇳🇱"),
                VpnServer(10, "South Korea", "KR", "Seoul", "211.233.10.88", 180, 50, true, false, "Gaming", "🇰🇷"),
                VpnServer(11, "India", "IN", "Mumbai", "103.111.45.20", 45, 60, false, false, "General", "🇮🇳"),
                VpnServer(12, "Australia", "AU", "Sydney", "139.130.4.5", 195, 28, true, false, "Streaming", "🇦🇺"),
                VpnServer(13, "Brazil", "BR", "São Paulo", "177.12.180.1", 140, 35, false, false, "General", "🇧🇷"),
                VpnServer(14, "UAE", "AE", "Dubai", "94.200.12.33", 68, 40, true, false, "General", "🇦🇪"),
                VpnServer(15, "Turkey", "TR", "Istanbul", "185.122.200.5", 88, 55, false, false, "General", "🇹🇷"),
                VpnServer(16, "Sweden", "SE", "Stockholm", "193.180.10.4", 92, 18, false, false, "Torrent", "🇸🇪"),
                VpnServer(17, "Norway", "NO", "Oslo", "185.12.100.1", 98, 14, false, false, "General", "🇳🇴"),
                VpnServer(18, "Switzerland", "CH", "Zurich", "179.43.140.2", 102, 22, true, false, "General", "🇨🇭"),
                VpnServer(19, "Italy", "IT", "Milan", "185.25.120.1", 108, 48, false, false, "Streaming", "🇮🇹"),
                VpnServer(20, "Spain", "ES", "Madrid", "185.10.180.2", 115, 38, false, false, "General", "🇪🇸"),
                VpnServer(21, "Finland", "FI", "Helsinki", "185.90.100.1", 110, 16, false, false, "General", "🇫🇮")
            )

            serverDao.insertServers(defaultServers)

            val defaultLogs = listOf(
                ConnectionLog(1, "San Francisco - US1", "United States", "14:20 Today", "2h 45m", 1240.0, "Stable"),
                ConnectionLog(2, "Frankfurt - DE1", "Germany", "Yesterday", "5h 12m", 4800.0, "Stable"),
                ConnectionLog(3, "Tokyo - JP1", "Japan", "2 days ago", "1h 05m", 850.0, "Latency Peak"),
                ConnectionLog(4, "London - UK1", "United Kingdom", "3 days ago", "3h 20m", 2100.0, "Stable")
            )

            defaultLogs.forEach { logDao.insertLog(it) }

            settingsDao.saveUserSettings(UserSettings())
        }
    }
}
