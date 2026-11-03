package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY createdAt ASC")
    fun getAllPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    fun getPlayerById(id: String): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun getPlayerByIdSync(id: String): PlayerEntity?

    @Query("SELECT * FROM players")
    suspend fun getAllPlayersSync(): List<PlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun deletePlayerById(id: String)

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
