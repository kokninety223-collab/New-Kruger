package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vps_servers")
data class VpsServer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val ip: String,
    val port: Int = 51820,
    val publicKey: String,
    val endpoint: String,
    val allowedIps: String = "0.0.0.0/0, ::/0",
    val dns: String = "1.1.1.1, 8.8.8.8",
    val presharedKey: String = "",
    val clientAddress: String = "10.0.0.2/32",
    val isActive: Boolean = true,
    val latencyMs: Long = -1L,
    val isOnline: Boolean = true,
    val lastCheckTime: Long = System.currentTimeMillis()
)
