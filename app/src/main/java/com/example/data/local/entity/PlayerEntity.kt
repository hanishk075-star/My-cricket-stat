package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Player

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val jerseyNumber: Int,
    val dateOfBirth: String,
    val battingHand: String,
    val bowlingArm: String,
    val bowlingStyle: String,
    val playingRole: String,
    val favouriteTeam: String,
    val description: String,
    val avatarColorHex: String,
    val createdAt: Long
) {
    fun toDomain(): Player = Player(
        id = id,
        fullName = fullName,
        jerseyNumber = jerseyNumber,
        dateOfBirth = dateOfBirth,
        battingHand = battingHand,
        bowlingArm = bowlingArm,
        bowlingStyle = bowlingStyle,
        playingRole = playingRole,
        favouriteTeam = favouriteTeam,
        description = description,
        avatarColorHex = avatarColorHex,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(player: Player): PlayerEntity = PlayerEntity(
            id = player.id,
            fullName = player.fullName,
            jerseyNumber = player.jerseyNumber,
            dateOfBirth = player.dateOfBirth,
            battingHand = player.battingHand,
            bowlingArm = player.bowlingArm,
            bowlingStyle = player.bowlingStyle,
            playingRole = player.playingRole,
            favouriteTeam = player.favouriteTeam,
            description = player.description,
            avatarColorHex = player.avatarColorHex,
            createdAt = player.createdAt
        )
    }
}
