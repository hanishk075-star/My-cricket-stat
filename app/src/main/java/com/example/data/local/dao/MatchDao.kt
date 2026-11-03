package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE playerId = :playerId ORDER BY matchDate DESC, createdAt DESC")
    fun getMatchesForPlayer(playerId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE playerId = :playerId ORDER BY matchDate DESC, createdAt DESC")
    suspend fun getMatchesForPlayerSync(playerId: String): List<MatchEntity>

    @Query("SELECT * FROM matches ORDER BY matchDate DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches ORDER BY matchDate DESC")
    suspend fun getAllMatchesSync(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getMatchById(id: String): Flow<MatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun deleteMatchById(id: String)

    @Query("DELETE FROM matches WHERE playerId = :playerId")
    suspend fun deleteMatchesForPlayer(playerId: String)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}
