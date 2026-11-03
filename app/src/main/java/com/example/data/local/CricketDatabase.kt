package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.MatchDao
import com.example.data.local.dao.PlayerDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.MatchEntity
import com.example.data.local.entity.PlayerEntity
import com.example.data.local.entity.QuizHistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PlayerEntity::class,
        MatchEntity::class,
        BookmarkEntity::class,
        QuizHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: CricketDatabase? = null

        fun getInstance(context: Context): CricketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricketDatabase::class.java,
                    "my_cricket_stats.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default starter profiles and matches on initial DB creation
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: CricketDatabase) {
            val player1 = PlayerEntity(
                id = "player_virat_18",
                fullName = "Virat K.",
                jerseyNumber = 18,
                dateOfBirth = "1994-11-05",
                battingHand = "Right-handed",
                bowlingArm = "Right-arm",
                bowlingStyle = "Medium pace",
                playingRole = "All-rounder",
                favouriteTeam = "India",
                description = "Top-order anchor and handy medium pacer.",
                avatarColorHex = "#059669",
                createdAt = System.currentTimeMillis() - 86400000L * 30
            )

            val player2 = PlayerEntity(
                id = "player_rohit_45",
                fullName = "Rohit S.",
                jerseyNumber = 45,
                dateOfBirth = "1993-04-30",
                battingHand = "Right-handed",
                bowlingArm = "Right-arm",
                bowlingStyle = "Off-spin",
                playingRole = "Batter",
                favouriteTeam = "Mumbai",
                description = "Aggressive stroke player with high boundary conversion.",
                avatarColorHex = "#2563EB",
                createdAt = System.currentTimeMillis() - 86400000L * 25
            )

            db.playerDao().insertPlayers(listOf(player1, player2))

            // Matches for Player 1: Matches the user prompt exact test case!
            val match1 = MatchEntity(
                id = "match_101",
                playerId = player1.id,
                matchDate = "2026-10-24",
                opponentName = "Thunder Cricket Club",
                playerTeam = "Emerald Kings",
                matchFormat = "T20",
                venue = "City Oval Ground",
                matchType = "Club cricket",
                scheduledOvers = 20,
                matchResult = "Won",
                matchNotes = "Solid match-winning performance with bat and ball.",
                didBat = true,
                runsScored = 45,
                ballsFaced = 30,
                fours = 5,
                sixes = 2,
                inningsEnded = "Caught",
                battingPosition = 3,
                didBowl = true,
                oversBowledStr = "4.2",
                legalBallsBowled = 26,
                maidens = 0,
                runsConceded = 26,
                wicketsTaken = 2,
                wides = 1,
                noBalls = 0,
                bowlingNotes = "Clean death-overs spell, 2 key wickets.",
                catches = 1,
                runOuts = 0,
                stumpings = 0,
                createdAt = System.currentTimeMillis() - 86400000L * 10
            )

            val match2 = MatchEntity(
                id = "match_102",
                playerId = player1.id,
                matchDate = "2026-10-18",
                opponentName = "Phoenix Strikers",
                playerTeam = "Emerald Kings",
                matchFormat = "T20",
                venue = "Riverside Park",
                matchType = "Tournament",
                scheduledOvers = 20,
                matchResult = "Won",
                matchNotes = "Top scored and stayed till the end.",
                didBat = true,
                runsScored = 78,
                ballsFaced = 46,
                fours = 8,
                sixes = 3,
                inningsEnded = "Not out",
                battingPosition = 3,
                didBowl = true,
                oversBowledStr = "4.0",
                legalBallsBowled = 24,
                maidens = 1,
                runsConceded = 18,
                wicketsTaken = 3,
                wides = 0,
                noBalls = 0,
                bowlingNotes = "Best bowling figures so far.",
                catches = 2,
                runOuts = 1,
                stumpings = 0,
                createdAt = System.currentTimeMillis() - 86400000L * 16
            )

            val match3 = MatchEntity(
                id = "match_103",
                playerId = player1.id,
                matchDate = "2026-10-05",
                opponentName = "Metro Knights",
                playerTeam = "Emerald Kings",
                matchFormat = "T20",
                venue = "Westfield Arena",
                matchType = "Club cricket",
                scheduledOvers = 20,
                matchResult = "Lost",
                matchNotes = "Close game lost by 6 runs.",
                didBat = true,
                runsScored = 32,
                ballsFaced = 22,
                fours = 3,
                sixes = 1,
                inningsEnded = "Bowled",
                battingPosition = 3,
                didBowl = true,
                oversBowledStr = "3.0",
                legalBallsBowled = 18,
                maidens = 0,
                runsConceded = 24,
                wicketsTaken = 1,
                wides = 2,
                noBalls = 0,
                bowlingNotes = "",
                catches = 0,
                runOuts = 0,
                stumpings = 0,
                createdAt = System.currentTimeMillis() - 86400000L * 29
            )

            // Match for Player 2 (independent)
            val match4 = MatchEntity(
                id = "match_201",
                playerId = player2.id,
                matchDate = "2026-10-20",
                opponentName = "Coastal XI",
                playerTeam = "Mumbai Titans",
                matchFormat = "ODI",
                venue = "Bayside Stadium",
                matchType = "Tournament",
                scheduledOvers = 50,
                matchResult = "Won",
                matchNotes = "Century as opening batter.",
                didBat = true,
                runsScored = 104,
                ballsFaced = 89,
                fours = 12,
                sixes = 4,
                inningsEnded = "Caught",
                battingPosition = 1,
                didBowl = false,
                oversBowledStr = "0.0",
                legalBallsBowled = 0,
                maidens = 0,
                runsConceded = 0,
                wicketsTaken = 0,
                wides = 0,
                noBalls = 0,
                bowlingNotes = "",
                catches = 1,
                runOuts = 0,
                stumpings = 0,
                createdAt = System.currentTimeMillis() - 86400000L * 14
            )

            db.matchDao().insertMatches(listOf(match1, match2, match3, match4))
        }
    }
}
