package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.VpsRepository
import com.example.data.VpsServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DispatchStrategy {
    HEALTHIEST,
    ROUND_ROBIN,
    RANDOM
}

data class SelectedVpsResult(
    val server: VpsServer?,
    val strategy: DispatchStrategy,
    val wgConfigText: String,
    val jsonResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)

class VpsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpsRepository
    val servers: StateFlow<List<VpsServer>>

    private val _isTestingHealth = MutableStateFlow(false)
    val isTestingHealth: StateFlow<Boolean> = _isTestingHealth.asStateFlow()

    private val _dispatchStrategy = MutableStateFlow(DispatchStrategy.HEALTHIEST)
    val dispatchStrategy: StateFlow<DispatchStrategy> = _dispatchStrategy.asStateFlow()

    private val _selectedDispatchResult = MutableStateFlow<SelectedVpsResult?>(null)
    val selectedDispatchResult: StateFlow<SelectedVpsResult?> = _selectedDispatchResult.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var roundRobinIndex = 0

    init {
        val dao = AppDatabase.getDatabase(application).vpsDao()
        repository = VpsRepository(dao)

        val rawServers = repository.allServers
        servers = combine(rawServers, _searchQuery) { list, query ->
            if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.ip.contains(query, ignoreCase = true) ||
                            it.endpoint.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            rawServers.collect { list ->
                if (list.isEmpty()) {
                    repository.seedInitialDataIfEmpty(list)
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStrategy(strategy: DispatchStrategy) {
        _dispatchStrategy.value = strategy
    }

    fun addServer(
        name: String,
        ip: String,
        port: Int,
        publicKey: String,
        endpoint: String,
        clientAddress: String,
        dns: String,
        allowedIps: String,
        presharedKey: String
    ) {
        viewModelScope.launch {
            val server = VpsServer(
                name = name.ifBlank { "VPS Server" },
                ip = ip.ifBlank { "127.0.0.1" },
                port = if (port > 0) port else 51820,
                publicKey = publicKey.ifBlank { "base64PublicKeyPlaceholder=" },
                endpoint = endpoint.ifBlank { "$ip:$port" },
                clientAddress = clientAddress.ifBlank { "10.8.0.2/32" },
                dns = dns.ifBlank { "1.1.1.1, 8.8.8.8" },
                allowedIps = allowedIps.ifBlank { "0.0.0.0/0, ::/0" },
                presharedKey = presharedKey,
                isActive = true
            )
            repository.insert(server)
        }
    }

    fun updateServer(server: VpsServer) {
        viewModelScope.launch {
            repository.update(server)
        }
    }

    fun deleteServer(server: VpsServer) {
        viewModelScope.launch {
            repository.delete(server)
        }
    }

    fun toggleActive(server: VpsServer) {
        viewModelScope.launch {
            repository.update(server.copy(isActive = !server.isActive))
        }
    }

    fun runHealthCheckAll() {
        viewModelScope.launch {
            _isTestingHealth.value = true
            val currentList = servers.value
            currentList.forEach { server ->
                repository.checkServerHealth(server)
            }
            _isTestingHealth.value = false
        }
    }

    fun testGetVpsEndpoint(clientPrivateKey: String = "<CLIENT_PRIVATE_KEY>") {
        viewModelScope.launch {
            val currentList = servers.value.filter { it.isActive }
            if (currentList.isEmpty()) {
                _selectedDispatchResult.value = SelectedVpsResult(
                    server = null,
                    strategy = _dispatchStrategy.value,
                    wgConfigText = "# Error: No active WireGuard VPS servers enabled",
                    jsonResponse = """{"status": "error", "message": "No active servers"}"""
                )
                return@launch
            }

            val chosen: VpsServer = when (_dispatchStrategy.value) {
                DispatchStrategy.RANDOM -> currentList.random()
                DispatchStrategy.ROUND_ROBIN -> {
                    val s = currentList[roundRobinIndex % currentList.size]
                    roundRobinIndex++
                    s
                }
                DispatchStrategy.HEALTHIEST -> {
                    // Find server with lowest latency
                    currentList.minByOrNull { if (it.latencyMs > 0) it.latencyMs else 9999 } ?: currentList.first()
                }
            }

            val pskLine = if (chosen.presharedKey.isNotBlank()) "PresharedKey = ${chosen.presharedKey}\n" else ""
            val wgConf = """
[Interface]
PrivateKey = $clientPrivateKey
Address = ${chosen.clientAddress}
DNS = ${chosen.dns}

[Peer]
PublicKey = ${chosen.publicKey}
$pskLine Endpoint = ${chosen.endpoint}
AllowedIPs = ${chosen.allowedIps}
PersistentKeepalive = 25
            """.trimIndent()

            val jsonStr = """
{
  "status": "success",
  "selection_strategy": "${_dispatchStrategy.value.name.lowercase()}",
  "selected_server": {
    "id": ${chosen.id},
    "name": "${chosen.name}",
    "ip": "${chosen.ip}",
    "port": ${chosen.port},
    "public_key": "${chosen.publicKey}",
    "endpoint": "${chosen.endpoint}",
    "client_address": "${chosen.clientAddress}",
    "dns": "${chosen.dns}",
    "allowed_ips": "${chosen.allowedIps}",
    "is_active": ${chosen.isActive},
    "latency_ms": ${chosen.latencyMs}
  },
  "wg_quick_config": "${wgConf.replace("\n", "\\n")}"
}
            """.trimIndent()

            _selectedDispatchResult.value = SelectedVpsResult(
                server = chosen,
                strategy = _dispatchStrategy.value,
                wgConfigText = wgConf,
                jsonResponse = jsonStr
            )
        }
    }
}
