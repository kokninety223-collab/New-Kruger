package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.VpsServer

@Composable
fun AddEditServerDialog(
    initialServer: VpsServer? = null,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        ip: String,
        port: Int,
        publicKey: String,
        endpoint: String,
        clientAddress: String,
        dns: String,
        allowedIps: String,
        presharedKey: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialServer?.name ?: "") }
    var ip by remember { mutableStateOf(initialServer?.ip ?: "") }
    var portStr by remember { mutableStateOf(initialServer?.port?.toString() ?: "51820") }
    var publicKey by remember { mutableStateOf(initialServer?.publicKey ?: "") }
    var endpoint by remember { mutableStateOf(initialServer?.endpoint ?: "") }
    var clientAddress by remember { mutableStateOf(initialServer?.clientAddress ?: "10.8.0.2/32") }
    var dns by remember { mutableStateOf(initialServer?.dns ?: "1.1.1.1, 8.8.8.8") }
    var allowedIps by remember { mutableStateOf(initialServer?.allowedIps ?: "0.0.0.0/0, ::/0") }
    var presharedKey by remember { mutableStateOf(initialServer?.presharedKey ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialServer == null) "Add WireGuard VPS" else "Edit WireGuard VPS") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Server Name") },
                    placeholder = { Text("e.g. SG Central VPS") },
                    modifier = Modifier.fillMaxWidth().testTag("input_server_name"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ip,
                    onValueChange = {
                        ip = it
                        if (endpoint.isBlank()) endpoint = "$it:$portStr"
                    },
                    label = { Text("VPS IP Address") },
                    placeholder = { Text("198.51.100.12") },
                    modifier = Modifier.fillMaxWidth().testTag("input_server_ip"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = portStr,
                    onValueChange = { portStr = it },
                    label = { Text("Port") },
                    placeholder = { Text("51820") },
                    modifier = Modifier.fillMaxWidth().testTag("input_server_port"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = publicKey,
                    onValueChange = { publicKey = it },
                    label = { Text("Public Key") },
                    placeholder = { Text("xT3k9QzLv8W1M4nR7p2A5s8D0f3G6h9J...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_server_public_key"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    label = { Text("Endpoint (Host:Port)") },
                    placeholder = { Text("198.51.100.12:51820") },
                    modifier = Modifier.fillMaxWidth().testTag("input_server_endpoint"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientAddress,
                    onValueChange = { clientAddress = it },
                    label = { Text("Client Address") },
                    placeholder = { Text("10.8.0.2/32") },
                    modifier = Modifier.fillMaxWidth().testTag("input_client_address"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dns,
                    onValueChange = { dns = it },
                    label = { Text("DNS Servers") },
                    placeholder = { Text("1.1.1.1, 8.8.8.8") },
                    modifier = Modifier.fillMaxWidth().testTag("input_dns"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = portStr.toIntOrNull() ?: 51820
                    onSave(
                        name,
                        ip,
                        p,
                        publicKey,
                        if (endpoint.isBlank()) "$ip:$p" else endpoint,
                        clientAddress,
                        dns,
                        allowedIps,
                        presharedKey
                    )
                },
                modifier = Modifier.testTag("save_server_button")
            ) {
                Text("Save Server")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_server_button")
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
