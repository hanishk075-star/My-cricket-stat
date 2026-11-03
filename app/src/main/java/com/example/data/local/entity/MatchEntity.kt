package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.MatchRecord

@Entity(
    tableName = "matches",
    indices = [Index(value = ["playerId"]), Index(value = ["matchDate"])]
)
data class MatchEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val matchDate: String,
    val opponentName: String,
    val playerTeam: String,
    val matchFormat: String,
    val venue: String,
    val matchType: String,
    val scheduledOvers: Int?,
    val matchResult: String,
    val matchNotes: String,

    // Batting
    val didBat: Boolean,
    val runsScored: Int,
    val ballsFaced: Int,
    val fours: Int,
    val sixes: Int,
    val inningsEnded: String,
    val battingPosition: Int?,

    // Bowling
    val didBowl: Boolean,
    val oversBowledStr: String,
    val legalBallsBowled: Int,
    val maidens: Int,
    val runsConceded: Int,
    val wicketsTaken: Int,
    val wides: Int,
    val noBalls: Int,
    val bowlingNotes: String,

    // Fielding
    val catches: Int,
    val runOuts: Int,
    val stumpings: Int,

    val createdAt: Long
) {
    fun toDomain(): MatchRecord = MatchRecord(
        id = id,
        playerId = playerId,
        matchDate = matchDate,
        opponentName = opponentName,
        playerTeam = playerTeam,
        matchFormat = matchFormat,
        venue = venue,
        matchType = matchType,
        scheduledOvers = scheduledOvers,
        matchResult = matchResult,
        matchNotes = matchNotes,
        didBat = didBat,
        runsScored = runsScored,
        ballsFaced = ballsFaced,
        fours = fours,
        sixes = sixes,
        inningsEnded = inningsEnded,
        battingPosition = battingPosition,
        didBowl = didBowl,
        oversBowledStr = oversBowledStr,
        legalBallsBowled = legalBallsBowled,
        maidens = maidens,
        runsConceded = runsConceded,
        wicketsTaken = wicketsTaken,
        wides = wides,
        noBalls = noBalls,
        bowlingNotes = bowlingNotes,
        catches = catches,
        runOuts = runOuts,
        stumpings = stumpings,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(m: MatchRecord): MatchEntity = MatchEntity(
            id = m.id,
            playerId = m.playerId,
            matchDate = m.matchDate,
            opponentName = m.opponentName,
            playerTeam = m.playerTeam,
            matchFormat = m.matchFormat,
            venue = m.venue,
            matchType = m.matchType,
            scheduledOvers = m.scheduledOvers,
            matchResult = m.matchResult,
            matchNotes = m.matchNotes,
            didBat = m.didBat,
            runsScored = m.runsScored,
            ballsFaced = m.ballsFaced,
            fours = m.fours,
            sixes = m.sixes,
            inningsEnded = m.inningsEnded,
            battingPosition = m.battingPosition,
            didBowl = m.didBowl,
            oversBowledStr = m.oversBowledStr,
            legalBallsBowled = m.legalBallsBowled,
            maidens = m.maidens,
            runsConceded = m.runsConceded,
            wicketsTaken = m.wicketsTaken,
            wides = m.wides,
            noBalls = m.noBalls,
            bowlingNotes = m.bowlingNotes,
            catches = m.catches,
            runOuts = m.runOuts,
            stumpings = m.stumpings,
            createdAt = m.createdAt
        )
    }
}
