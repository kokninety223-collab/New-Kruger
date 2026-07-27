package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.VpsServer
import com.example.ui.VpsViewModel
import com.example.ui.components.AddEditServerDialog
import com.example.ui.components.ServerCard

@Composable
fun ServerListScreen(
    viewModel: VpsViewModel,
    modifier: Modifier = Modifier
) {
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isTesting by viewModel.isTestingHealth.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingServer by remember { mutableStateOf<VpsServer?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("fab_add_server")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add VPS Server")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WireGuard Nodes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${servers.count { it.isActive }} Active VPS Servers Registered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.runHealthCheckAll() },
                    enabled = !isTesting,
                    modifier = Modifier.testTag("check_all_pings_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ping All")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by location, IP or endpoint...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_server_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (servers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnLock,
                            contentDescription = null,
                            modifier = Modifier.width(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No WireGuard VPS Servers Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to add your first VPS configuration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(servers, key = { it.id }) { server ->
                        ServerCard(
                            server = server,
                            onToggleActive = { viewModel.toggleActive(server) },
                            onEdit = { editingServer = server },
                            onDelete = { viewModel.deleteServer(server) },
                            onTestPing = { viewModel.runHealthCheckAll() }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog || editingServer != null) {
        AddEditServerDialog(
            initialServer = editingServer,
            onDismiss = {
                showAddDialog = false
                editingServer = null
            },
            onSave = { name, ip, port, publicKey, endpoint, clientAddress, dns, allowedIps, presharedKey ->
                if (editingServer != null) {
                    viewModel.updateServer(
                        editingServer!!.copy(
                            name = name,
                            ip = ip,
                            port = port,
                            publicKey = publicKey,
                            endpoint = endpoint,
                            clientAddress = clientAddress,
                            dns = dns,
                            allowedIps = allowedIps,
                            presharedKey = presharedKey
                        )
                    )
                } else {
                    viewModel.addServer(
                        name, ip, port, publicKey, endpoint, clientAddress, dns, allowedIps, presharedKey
                    )
                }
                showAddDialog = false
                editingServer = null
            }
        )
    }
}
