package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.DispatchStrategy
import com.example.ui.VpsViewModel
import com.example.ui.components.CodeViewerCard

@Composable
fun EndpointTestScreen(
    viewModel: VpsViewModel,
    modifier: Modifier = Modifier
) {
    val currentStrategy by viewModel.dispatchStrategy.collectAsStateWithLifecycle()
    val testResult by viewModel.selectedDispatchResult.collectAsStateWithLifecycle()
    var clientPrivateKey by remember { mutableStateOf("yAnBf5qL8s0m2N4p6R8t0V2x4Z6b8D0f2H4j6L8n0=") }
    var outputTabState by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Api,
                contentDescription = null,
                modifier = Modifier.width(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "GET /get-vps Tester",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Simulate active server health check & config dispatch algorithm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selection Strategy",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentStrategy == DispatchStrategy.HEALTHIEST,
                        onClick = { viewModel.setStrategy(DispatchStrategy.HEALTHIEST) },
                        label = { Text("Healthiest (Low Latency)") },
                        modifier = Modifier.testTag("strategy_chip_healthiest")
                    )
                    FilterChip(
                        selected = currentStrategy == DispatchStrategy.ROUND_ROBIN,
                        onClick = { viewModel.setStrategy(DispatchStrategy.ROUND_ROBIN) },
                        label = { Text("Round Robin") },
                        modifier = Modifier.testTag("strategy_chip_round_robin")
                    )
                    FilterChip(
                        selected = currentStrategy == DispatchStrategy.RANDOM,
                        onClick = { viewModel.setStrategy(DispatchStrategy.RANDOM) },
                        label = { Text("Random") },
                        modifier = Modifier.testTag("strategy_chip_random")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = clientPrivateKey,
                    onValueChange = { clientPrivateKey = it },
                    label = { Text("Client Private Key (Optional for wg-quick)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_client_key"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.testGetVpsEndpoint(clientPrivateKey) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("execute_get_vps_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Execute GET /get-vps Request")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (testResult != null) {
            Text(
                text = "Dispatch Result (${testResult!!.strategy.name})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            TabRow(selectedTabIndex = outputTabState) {
                Tab(
                    selected = outputTabState == 0,
                    onClick = { outputTabState = 0 },
                    text = { Text("JSON Response") }
                )
                Tab(
                    selected = outputTabState == 1,
                    onClick = { outputTabState = 1 },
                    text = { Text("WireGuard .conf") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (outputTabState == 0) {
                CodeViewerCard(
                    title = "GET /get-vps JSON Output",
                    code = testResult!!.jsonResponse,
                    language = "json"
                )
            } else {
                CodeViewerCard(
                    title = "wg0.conf Output",
                    code = testResult!!.wgConfigText,
                    language = "ini"
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap 'Execute GET /get-vps Request' above to test the endpoint selection.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
