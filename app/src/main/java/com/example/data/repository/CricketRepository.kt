package com.example.data.repository

import com.example.data.local.CricketDatabase
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.MatchEntity
import com.example.data.local.entity.PlayerEntity
import com.example.data.local.entity.QuizHistoryEntity
import com.example.data.model.CareerStatistics
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.data.model.QuizResult
import com.example.engine.CricketStatsEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class CricketRepository(private val database: CricketDatabase) {
    private val playerDao = database.playerDao()
    private val matchDao = database.matchDao()
    private val bookmarkDao = database.bookmarkDao()

    val allPlayers: Flow<List<Player>> = playerDao.getAllPlayers().map { list ->
        list.map { it.toDomain() }
    }

    fun getMatchesForPlayer(playerId: String): Flow<List<MatchRecord>> =
        matchDao.getMatchesForPlayer(playerId).map { list ->
            list.map { it.toDomain() }
        }

    val bookmarkedTopicIds: Flow<Set<String>> = bookmarkDao.getAllBookmarks().map { list ->
        list.map { it.topicId }.toSet()
    }

    val quizHistory: Flow<List<QuizResult>> = bookmarkDao.getQuizHistory().map { list ->
        list.map {
            QuizResult(
                totalQuestions = it.totalQuestions,
                correctAnswers = it.correctAnswers,
                scorePercentage = it.scorePercentage,
                dateAttempted = it.attemptedAt
            )
        }
    }

    suspend fun addPlayer(player: Player) {
        playerDao.insertPlayer(PlayerEntity.fromDomain(player))
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(PlayerEntity.fromDomain(player))
    }

    suspend fun deletePlayer(playerId: String) {
        matchDao.deleteMatchesForPlayer(playerId)
        playerDao.deletePlayerById(playerId)
    }

    suspend fun addMatch(match: MatchRecord) {
        matchDao.insertMatch(MatchEntity.fromDomain(match))
    }

    suspend fun updateMatch(match: MatchRecord) {
        matchDao.updateMatch(MatchEntity.fromDomain(match))
    }

    suspend fun deleteMatch(matchId: String) {
        matchDao.deleteMatchById(matchId)
    }

    suspend fun toggleBookmark(topicId: String, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            bookmarkDao.deleteBookmark(topicId)
        } else {
            bookmarkDao.insertBookmark(BookmarkEntity(topicId = topicId))
        }
    }

    suspend fun recordQuizResult(result: QuizResult) {
        bookmarkDao.insertQuizHistory(
            QuizHistoryEntity(
                totalQuestions = result.totalQuestions,
                correctAnswers = result.correctAnswers,
                scorePercentage = result.scorePercentage,
                attemptedAt = result.dateAttempted
            )
        )
    }

    fun calculateCareerStats(matches: List<MatchRecord>, playerId: String): CareerStatistics {
        var battingInnings = 0
        var dismissals = 0
        var notOuts = 0
        var totalRuns = 0
        var highestScore = 0
        var highestScoreNotOut = false
        var totalBallsFaced = 0
        var totalFours = 0
        var totalSixes = 0
        var fifties = 0
        var hundreds = 0
        var ducks = 0

        var bowlingInnings = 0
        var totalLegalBalls = 0
        var totalRunsConceded = 0
        var totalWickets = 0
        var maidens = 0
        var wides = 0
        var noBalls = 0
        var bestWickets = 0
        var bestRuns = Int.MAX_VALUE
        var hasBestBowling = false
        var threeWickets = 0
        var fiveWickets = 0

        var totalCatches = 0
        var totalRunOuts = 0
        var totalStumpings = 0

        for (m in matches) {
            // Fielding
            totalCatches += m.catches
            totalRunOuts += m.runOuts
            totalStumpings += m.stumpings

            // Batting
            if (m.didBat) {
                battingInnings++
                val runs = m.runsScored
                totalRuns += runs
                totalBallsFaced += m.ballsFaced
                totalFours += m.fours
                totalSixes += m.sixes

                val isDismissed = CricketStatsEngine.isDismissal(m.inningsEnded)
                if (isDismissed) {
                    dismissals++
                    if (runs == 0) ducks++
                } else {
                    notOuts++
                }

                if (runs > highestScore || (runs == highestScore && !isDismissed)) {
                    highestScore = runs
                    highestScoreNotOut = !isDismissed
                }

                if (runs >= 100) {
                    hundreds++
                } else if (runs >= 50) {
                    fifties++
                }
            }

            // Bowling
            if (m.didBowl) {
                bowlingInnings++
                val legalBalls = m.legalBallsBowled
                totalLegalBalls += legalBalls
                totalRunsConceded += m.runsConceded
                totalWickets += m.wicketsTaken
                maidens += m.maidens
                wides += m.wides
                noBalls += m.noBalls

                if (m.wicketsTaken >= 5) {
                    fiveWickets++
                } else if (m.wicketsTaken >= 3) {
                    threeWickets++
                }

                if (legalBalls > 0 || m.wicketsTaken > 0) {
                    if (!hasBestBowling ||
                        m.wicketsTaken > bestWickets ||
                        (m.wicketsTaken == bestWickets && m.runsConceded < bestRuns)
                    ) {
                        bestWickets = m.wicketsTaken
                        bestRuns = m.runsConceded
                        hasBestBowling = true
                    }
                }
            }
        }

        val battingAvg = CricketStatsEngine.calculateBattingAverage(totalRuns, dismissals)
        val battingSR = CricketStatsEngine.calculateBattingStrikeRate(totalRuns, totalBallsFaced)
        val boundaryPct = CricketStatsEngine.calculateBoundaryPercentage(totalRuns, totalFours, totalSixes)

        val bowlingEcon = CricketStatsEngine.calculateBowlingEconomy(totalRunsConceded, totalLegalBalls)
        val bowlingAvg = CricketStatsEngine.calculateBowlingAverage(totalRunsConceded, totalWickets)
        val bowlingSR = CricketStatsEngine.calculateBowlingStrikeRate(totalLegalBalls, totalWickets)
        val oversStr = CricketStatsEngine.legalBallsToOversString(totalLegalBalls)

        return CareerStatistics(
            playerId = playerId,
            totalMatches = matches.size,
            battingInnings = battingInnings,
            dismissals = dismissals,
            notOuts = notOuts,
            totalRuns = totalRuns,
            highestScore = highestScore,
            highestScoreNotOut = highestScoreNotOut,
            battingAverage = battingAvg,
            battingStrikeRate = battingSR,
            totalBallsFaced = totalBallsFaced,
            totalFours = totalFours,
            totalSixes = totalSixes,
            totalBoundaries = totalFours + totalSixes,
            boundaryPercentage = boundaryPct,
            fiftiesCount = fifties,
            hundredsCount = hundreds,
            ducksCount = ducks,
            bowlingInnings = bowlingInnings,
            totalLegalBallsBowled = totalLegalBalls,
            totalOversString = oversStr,
            totalRunsConceded = totalRunsConceded,
            totalWickets = totalWickets,
            bowlingEconomy = bowlingEcon,
            bowlingAverage = bowlingAvg,
            bowlingStrikeRate = bowlingSR,
            totalMaidens = maidens,
            totalWides = wides,
            totalNoBalls = noBalls,
            bestBowlingWickets = if (hasBestBowling) bestWickets else 0,
            bestBowlingRuns = if (hasBestBowling && bestRuns != Int.MAX_VALUE) bestRuns else 0,
            hasBestBowling = hasBestBowling,
            threeWicketHauls = threeWickets,
            fiveWicketHauls = fiveWickets,
            totalCatches = totalCatches,
            totalRunOuts = totalRunOuts,
            totalStumpings = totalStumpings
        )
    }

    suspend fun exportBackupJson(): String {
        val players = playerDao.getAllPlayersSync()
        val matches = matchDao.getAllMatchesSync()

        val root = JSONObject()
        root.put("app", "MyCricketStats")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val playersArray = JSONArray()
        for (p in players) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("fullName", p.fullName)
            pObj.put("jerseyNumber", p.jerseyNumber)
            pObj.put("dateOfBirth", p.dateOfBirth)
            pObj.put("battingHand", p.battingHand)
            pObj.put("bowlingArm", p.bowlingArm)
            pObj.put("bowlingStyle", p.bowlingStyle)
            pObj.put("playingRole", p.playingRole)
            pObj.put("favouriteTeam", p.favouriteTeam)
            pObj.put("description", p.description)
            pObj.put("avatarColorHex", p.avatarColorHex)
            pObj.put("createdAt", p.createdAt)
            playersArray.put(pObj)
        }
        root.put("players", playersArray)

        val matchesArray = JSONArray()
        for (m in matches) {
            val mObj = JSONObject()
            mObj.put("id", m.id)
            mObj.put("playerId", m.playerId)
            mObj.put("matchDate", m.matchDate)
            mObj.put("opponentName", m.opponentName)
            mObj.put("playerTeam", m.playerTeam)
            mObj.put("matchFormat", m.matchFormat)
            mObj.put("venue", m.venue)
            mObj.put("matchType", m.matchType)
            mObj.put("scheduledOvers", m.scheduledOvers ?: JSONObject.NULL)
            mObj.put("matchResult", m.matchResult)
            mObj.put("matchNotes", m.matchNotes)
            mObj.put("didBat", m.didBat)
            mObj.put("runsScored", m.runsScored)
            mObj.put("ballsFaced", m.ballsFaced)
            mObj.put("fours", m.fours)
            mObj.put("sixes", m.sixes)
            mObj.put("inningsEnded", m.inningsEnded)
            mObj.put("battingPosition", m.battingPosition ?: JSONObject.NULL)
            mObj.put("didBowl", m.didBowl)
            mObj.put("oversBowledStr", m.oversBowledStr)
            mObj.put("legalBallsBowled", m.legalBallsBowled)
            mObj.put("maidens", m.maidens)
            mObj.put("runsConceded", m.runsConceded)
            mObj.put("wicketsTaken", m.wicketsTaken)
            mObj.put("wides", m.wides)
            mObj.put("noBalls", m.noBalls)
            mObj.put("bowlingNotes", m.bowlingNotes)
            mObj.put("catches", m.catches)
            mObj.put("runOuts", m.runOuts)
            mObj.put("stumpings", m.stumpings)
            mObj.put("createdAt", m.createdAt)
            matchesArray.put(mObj)
        }
        root.put("matches", matchesArray)

        return root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String, merge: Boolean): Result<Int> {
        return try {
            val root = JSONObject(jsonString)
            val playersArray = root.optJSONArray("players") ?: JSONArray()
            val matchesArray = root.optJSONArray("matches") ?: JSONArray()

            val newPlayers = mutableListOf<PlayerEntity>()
            for (i in 0 until playersArray.length()) {
                val p = playersArray.getJSONObject(i)
                newPlayers.add(
                    PlayerEntity(
                        id = p.optString("id", UUID.randomUUID().toString()),
                        fullName = p.optString("fullName", "Cricket Player"),
                        jerseyNumber = p.optInt("jerseyNumber", 0),
                        dateOfBirth = p.optString("dateOfBirth", ""),
                        battingHand = p.optString("battingHand", "Right-handed"),
                        bowlingArm = p.optString("bowlingArm", "Right-arm"),
                        bowlingStyle = p.optString("bowlingStyle", "Medium pace"),
                        playingRole = p.optString("playingRole", "All-rounder"),
                        favouriteTeam = p.optString("favouriteTeam", ""),
                        description = p.optString("description", ""),
                        avatarColorHex = p.optString("avatarColorHex", "#059669"),
                        createdAt = p.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val newMatches = mutableListOf<MatchEntity>()
            for (i in 0 until matchesArray.length()) {
                val m = matchesArray.getJSONObject(i)
                newMatches.add(
                    MatchEntity(
                        id = m.optString("id", UUID.randomUUID().toString()),
                        playerId = m.optString("playerId", ""),
                        matchDate = m.optString("matchDate", "2026-01-01"),
                        opponentName = m.optString("opponentName", "Opponent"),
                        playerTeam = m.optString("playerTeam", "My Team"),
                        matchFormat = m.optString("matchFormat", "T20"),
                        venue = m.optString("venue", ""),
                        matchType = m.optString("matchType", "Club cricket"),
                        scheduledOvers = if (m.isNull("scheduledOvers")) null else m.optInt("scheduledOvers"),
                        matchResult = m.optString("matchResult", "Won"),
                        matchNotes = m.optString("matchNotes", ""),
                        didBat = m.optBoolean("didBat", true),
                        runsScored = m.optInt("runsScored", 0),
                        ballsFaced = m.optInt("ballsFaced", 0),
                        fours = m.optInt("fours", 0),
                        sixes = m.optInt("sixes", 0),
                        inningsEnded = m.optString("inningsEnded", "Not out"),
                        battingPosition = if (m.isNull("battingPosition")) null else m.optInt("battingPosition"),
                        didBowl = m.optBoolean("didBowl", false),
                        oversBowledStr = m.optString("oversBowledStr", "0.0"),
                        legalBallsBowled = m.optInt("legalBallsBowled", 0),
                        maidens = m.optInt("maidens", 0),
                        runsConceded = m.optInt("runsConceded", 0),
                        wicketsTaken = m.optInt("wicketsTaken", 0),
                        wides = m.optInt("wides", 0),
                        noBalls = m.optInt("noBalls", 0),
                        bowlingNotes = m.optString("bowlingNotes", ""),
                        catches = m.optInt("catches", 0),
                        runOuts = m.optInt("runOuts", 0),
                        stumpings = m.optInt("stumpings", 0),
                        createdAt = m.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            if (!merge) {
                matchDao.deleteAllMatches()
                playerDao.deleteAllPlayers()
            }

            playerDao.insertPlayers(newPlayers)
            matchDao.insertMatches(newMatches)
            Result.success(newPlayers.size + newMatches.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearAllData() {
        matchDao.deleteAllMatches()
        playerDao.deleteAllPlayers()
        bookmarkDao.clearQuizHistory()
    }
}
