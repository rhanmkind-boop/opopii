package com.example.vpn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ConnectionLog
import com.example.data.UserSettings
import com.example.data.VpnRepository
import com.example.data.VpnServer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class SpeedTestResult(
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val stage: String = "Idle", // Testing Ping, Testing Download, Testing Upload, Complete
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val pingMs: Int = 0,
    val jitterMs: Int = 0,
    val qualityGrade: String = "A+"
)

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = VpnRepository(db.vpnServerDao(), db.connectionLogDao(), db.userSettingsDao())

    val allServers: StateFlow<List<VpnServer>> = repository.allServers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentLogs: StateFlow<List<ConnectionLog>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings?> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val _connectionState = MutableStateFlow(VpnState.DISCONNECTED)
    val connectionState: StateFlow<VpnState> = _connectionState.asStateFlow()

    private val _selectedServer = MutableStateFlow<VpnServer?>(null)
    val selectedServer: StateFlow<VpnServer?> = _selectedServer.asStateFlow()

    private val _sessionSeconds = MutableStateFlow(0L)
    val sessionSeconds: StateFlow<Long> = _sessionSeconds.asStateFlow()

    private val _downloadSpeed = MutableStateFlow(0.0)
    val downloadSpeed: StateFlow<Double> = _downloadSpeed.asStateFlow()

    private val _uploadSpeed = MutableStateFlow(0.0)
    val uploadSpeed: StateFlow<Double> = _uploadSpeed.asStateFlow()

    private val _currentPing = MutableStateFlow(24)
    val currentPing: StateFlow<Int> = _currentPing.asStateFlow()

    private val _adsBlocked = MutableStateFlow(8432)
    val adsBlocked: StateFlow<Int> = _adsBlocked.asStateFlow()

    private val _malwareBlocked = MutableStateFlow(1204)
    val malwareBlocked: StateFlow<Int> = _malwareBlocked.asStateFlow()

    private val _dataTodayMb = MutableStateFlow(842.6)
    val dataTodayMb: StateFlow<Double> = _dataTodayMb.asStateFlow()

    private val _speedTest = MutableStateFlow(SpeedTestResult())
    val speedTest: StateFlow<SpeedTestResult> = _speedTest.asStateFlow()

    private var connectionTimerJob: Job? = null
    private var speedFluctuationJob: Job? = null

    init {
        viewModelScope.launch {
            allServers.collectLatest { servers ->
                if (_selectedServer.value == null && servers.isNotEmpty()) {
                    // Default to Singapore or first server
                    _selectedServer.value = servers.find { it.countryCode == "SG" } ?: servers.first()
                }
            }
        }
    }

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        if (_connectionState.value == VpnState.CONNECTED) {
            // Reconnect to new server
            toggleConnection() // disconnect
            viewModelScope.launch {
                delay(800)
                toggleConnection() // connect
            }
        }
    }

    fun toggleFavorite(server: VpnServer) {
        viewModelScope.launch {
            repository.toggleFavorite(server.id, server.isFavorite)
        }
    }

    fun toggleConnection() {
        when (_connectionState.value) {
            VpnState.DISCONNECTED -> startConnect()
            VpnState.CONNECTED -> startDisconnect()
            else -> {}
        }
    }

    private fun startConnect() {
        viewModelScope.launch {
            _connectionState.value = VpnState.CONNECTING
            delay(1200) // Connection handshake simulation
            _connectionState.value = VpnState.CONNECTED
            _sessionSeconds.value = 0L

            val server = _selectedServer.value
            _currentPing.value = server?.pingMs ?: 24
            _downloadSpeed.value = Random.nextDouble(35.0, 85.0)
            _uploadSpeed.value = Random.nextDouble(12.0, 32.0)

            // Start timer job
            connectionTimerJob?.cancel()
            connectionTimerJob = viewModelScope.launch {
                while (_connectionState.value == VpnState.CONNECTED) {
                    delay(1000)
                    _sessionSeconds.value += 1
                    _dataTodayMb.value += Random.nextDouble(0.05, 0.25)
                    if (_sessionSeconds.value % 5 == 0L) {
                        _adsBlocked.value += Random.nextInt(1, 4)
                    }
                }
            }

            // Start speed fluctuation job
            speedFluctuationJob?.cancel()
            speedFluctuationJob = viewModelScope.launch {
                while (_connectionState.value == VpnState.CONNECTED) {
                    delay(2000)
                    _downloadSpeed.value = (35.0 + Random.nextDouble(-8.0, 15.0)).coerceAtLeast(5.0)
                    _uploadSpeed.value = (18.0 + Random.nextDouble(-4.0, 8.0)).coerceAtLeast(2.0)
                    _currentPing.value = ((server?.pingMs ?: 24) + Random.nextInt(-3, 5)).coerceAtLeast(8)
                }
            }
        }
    }

    private fun startDisconnect() {
        viewModelScope.launch {
            _connectionState.value = VpnState.DISCONNECTING
            connectionTimerJob?.cancel()
            speedFluctuationJob?.cancel()

            val server = _selectedServer.value
            val durationText = formatDuration(_sessionSeconds.value)
            val dataUsedMb = (_sessionSeconds.value * 0.15).coerceAtLeast(0.5)

            if (server != null && _sessionSeconds.value > 2) {
                val timeStr = SimpleDateFormat("HH:mm 'Today'", Locale.getDefault()).format(Date())
                repository.addLog(
                    ConnectionLog(
                        serverName = "${server.cityName} - ${server.countryCode}",
                        serverCountry = server.countryName,
                        connectedAt = timeStr,
                        durationFormatted = durationText,
                        dataUsedMb = String.format(Locale.US, "%.1f", dataUsedMb).toDouble(),
                        status = "Stable"
                    )
                )
            }

            delay(600)
            _connectionState.value = VpnState.DISCONNECTED
            _sessionSeconds.value = 0L
            _downloadSpeed.value = 0.0
            _uploadSpeed.value = 0.0
        }
    }

    fun updateSettings(newSettings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    fun runSpeedTest() {
        viewModelScope.launch {
            _speedTest.value = SpeedTestResult(isRunning = true, progress = 0.05f, stage = "Pinging Server...")
            delay(800)
            val ping = Random.nextInt(12, 35)
            val jitter = Random.nextInt(1, 5)

            _speedTest.value = SpeedTestResult(isRunning = true, progress = 0.35f, stage = "Testing Download Speed...", pingMs = ping, jitterMs = jitter)
            for (p in 35..70 step 5) {
                delay(120)
                val liveDl = Random.nextDouble(60.0, 120.0)
                _speedTest.value = _speedTest.value.copy(progress = p / 100f, downloadMbps = liveDl)
            }

            val finalDl = Random.nextDouble(85.0, 140.0)
            _speedTest.value = _speedTest.value.copy(progress = 0.70f, stage = "Testing Upload Speed...", downloadMbps = finalDl)

            for (p in 70..100 step 5) {
                delay(120)
                val liveUl = Random.nextDouble(25.0, 55.0)
                _speedTest.value = _speedTest.value.copy(progress = p / 100f, uploadMbps = liveUl)
            }

            val finalUl = Random.nextDouble(38.0, 65.0)
            _speedTest.value = SpeedTestResult(
                isRunning = false,
                progress = 1.0f,
                stage = "Completed",
                downloadMbps = finalDl,
                uploadMbps = finalUl,
                pingMs = ping,
                jitterMs = jitter,
                qualityGrade = if (finalDl > 100) "A+" else "A"
            )
        }
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) {
            String.format(Locale.US, "%02dh %02dm %02ds", h, m, s)
        } else {
            String.format(Locale.US, "%02dm %02ds", m, s)
        }
    }
}
