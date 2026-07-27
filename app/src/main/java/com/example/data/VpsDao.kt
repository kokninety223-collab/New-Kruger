package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VpsDao {
    @Query("SELECT * FROM vps_servers ORDER BY id ASC")
    fun getAllServers(): Flow<List<VpsServer>>

    @Query("SELECT * FROM vps_servers WHERE isActive = 1")
    fun getActiveServers(): Flow<List<VpsServer>>

    @Query("SELECT * FROM vps_servers WHERE id = :id")
    suspend fun getServerById(id: Int): VpsServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: VpsServer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpsServer>)

    @Update
    suspend fun updateServer(server: VpsServer)

    @Delete
    suspend fun deleteServer(server: VpsServer)

    @Query("DELETE FROM vps_servers WHERE id = :id")
    suspend fun deleteServerById(id: Int)

    @Query("DELETE FROM vps_servers")
    suspend fun deleteAll()
}
