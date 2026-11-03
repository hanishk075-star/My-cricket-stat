package com.example.data.model

data class Player(
    val id: String,
    val fullName: String,
    val jerseyNumber: Int = 18,
    val dateOfBirth: String = "",
    val battingHand: String = "Right-handed", // Right-handed, Left-handed
    val bowlingArm: String = "Right-arm",     // Right-arm, Left-arm
    val bowlingStyle: String = "Fast-medium", // Fast, Fast-medium, Medium pace, Off-spin, Leg-spin, Left-arm orthodox, Left-arm wrist spin, Other
    val playingRole: String = "All-rounder",  // Batter, Bowler, All-rounder, Wicketkeeper
    val favouriteTeam: String = "",
    val description: String = "",
    val avatarColorHex: String = "#059669",
    val createdAt: Long = System.currentTimeMillis()
)

data class MatchRecord(
    val id: String,
    val playerId: String,
    val matchDate: String, // YYYY-MM-DD
    val opponentName: String,
    val playerTeam: String,
    val matchFormat: String, // T5, T10, T20, ODI, Test, Custom
    val venue: String = "",
    val matchType: String = "Club cricket", // Gully cricket, School cricket, Club cricket, Tournament, Friendly, Other
    val scheduledOvers: Int? = null,
    val matchResult: String = "Won", // Won, Lost, Draw, Tie, No Result
    val matchNotes: String = "",
    
    // Batting
    val didBat: Boolean = true,
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val inningsEnded: String = "Not out", // Not out, Bowled, Caught, LBW, Run out, Stumped, Hit wicket, Retired out, Other dismissal
    val battingPosition: Int? = null,

    // Bowling
    val didBowl: Boolean = false,
    val oversBowledStr: String = "0.0",
    val legalBallsBowled: Int = 0,
    val maidens: Int = 0,
    val runsConceded: Int = 0,
    val wicketsTaken: Int = 0,
    val wides: Int = 0,
    val noBalls: Int = 0,
    val bowlingNotes: String = "",

    // Fielding
    val catches: Int = 0,
    val runOuts: Int = 0,
    val stumpings: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)

data class CareerStatistics(
    val playerId: String,
    val totalMatches: Int = 0,

    // Batting Aggregates
    val battingInnings: Int = 0,
    val dismissals: Int = 0,
    val notOuts: Int = 0,
    val totalRuns: Int = 0,
    val highestScore: Int = 0,
    val highestScoreNotOut: Boolean = false,
    val battingAverage: Double? = null,
    val battingStrikeRate: Double? = null,
    val totalBallsFaced: Int = 0,
    val totalFours: Int = 0,
    val totalSixes: Int = 0,
    val totalBoundaries: Int = 0,
    val boundaryPercentage: Double? = null,
    val fiftiesCount: Int = 0,
    val hundredsCount: Int = 0,
    val ducksCount: Int = 0,

    // Bowling Aggregates
    val bowlingInnings: Int = 0,
    val totalLegalBallsBowled: Int = 0,
    val totalOversString: String = "0.0",
    val totalRunsConceded: Int = 0,
    val totalWickets: Int = 0,
    val bowlingEconomy: Double? = null,
    val bowlingAverage: Double? = null,
    val bowlingStrikeRate: Double? = null,
    val totalMaidens: Int = 0,
    val totalWides: Int = 0,
    val totalNoBalls: Int = 0,
    val bestBowlingWickets: Int = 0,
    val bestBowlingRuns: Int = 0,
    val hasBestBowling: Boolean = false,
    val threeWicketHauls: Int = 0,
    val fiveWicketHauls: Int = 0,

    // Fielding Aggregates
    val totalCatches: Int = 0,
    val totalRunOuts: Int = 0,
    val totalStumpings: Int = 0
) {
    val highestScoreDisplay: String
        get() = if (battingInnings == 0) "--" else if (highestScoreNotOut) "$highestScore*" else "$highestScore"

    val bestBowlingDisplay: String
        get() = if (!hasBestBowling || totalWickets == 0 && bestBowlingRuns == 0) "--" else "$bestBowlingWickets/$bestBowlingRuns"
}

data class KnowledgeItem(
    val id: String,
    val title: String,
    val category: String, // "Rules", "Dismissals", "Extras", "Formats", "Terminology", "Advanced"
    val summary: String,
    val fullExplanation: String,
    val ruleReference: String, // MCC Law or ICC Regulation
    val example: String = "",
    val tags: List<String> = emptyList(),
    val isBookmarked: Boolean = false
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val category: String
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercentage: Int,
    val dateAttempted: Long = System.currentTimeMillis()
)
