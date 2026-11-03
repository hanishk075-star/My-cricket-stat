package com.example.engine

import java.util.Locale

object CricketStatsEngine {

    /**
     * Converts cricket overs notation (e.g., "4.2", "4.0", "4", "0.5") into integer legal deliveries.
     * Note: "4.6" or "4.7" is INVALID cricket notation because an over only has 6 balls (indices 0..5).
     * Returns null if invalid.
     */
    fun oversStringToLegalBalls(oversStr: String?): Int? {
        if (oversStr.isNullOrBlank()) return 0
        val trimmed = oversStr.trim()
        val parts = trimmed.split(".")
        return when (parts.size) {
            1 -> {
                val overs = parts[0].toIntOrNull() ?: return null
                if (overs < 0) return null
                overs * 6
            }
            2 -> {
                val overs = parts[0].toIntOrNull() ?: return null
                val balls = parts[1].toIntOrNull() ?: return null
                if (overs < 0 || balls < 0 || balls > 5) return null
                (overs * 6) + balls
            }
            else -> null
        }
    }

    /**
     * Converts legal balls back into standard cricket overs notation string (e.g. 26 -> "4.2", 24 -> "4.0").
     */
    fun legalBallsToOversString(legalBalls: Int): String {
        if (legalBalls <= 0) return "0.0"
        val completedOvers = legalBalls / 6
        val remainingBalls = legalBalls % 6
        return "$completedOvers.$remainingBalls"
    }

    /**
     * Checks if a dismissal type counts as a batsman dismissal.
     * "Not out" and "Retired not out" do NOT count as dismissals.
     */
    fun isDismissal(inningsEnded: String?): Boolean {
        if (inningsEnded.isNullOrBlank()) return false
        val normalized = inningsEnded.trim().lowercase(Locale.ROOT)
        return normalized != "not out" && normalized != "retired not out"
    }

    /**
     * Checks if a dismissal credits a wicket to the bowler.
     * Run outs, obstructing the field, and retired out do NOT count toward bowler wickets.
     */
    fun isBowlerCreditedDismissal(dismissalType: String?): Boolean {
        if (dismissalType.isNullOrBlank()) return false
        val normalized = dismissalType.trim().lowercase(Locale.ROOT)
        return when (normalized) {
            "bowled", "caught", "lbw", "stumped", "hit wicket" -> true
            else -> false
        }
    }

    /**
     * Calculates batting average: Total runs / dismissals.
     * Returns null if dismissals == 0 (no division by zero).
     */
    fun calculateBattingAverage(totalRuns: Int, dismissals: Int): Double? {
        if (dismissals <= 0) return null
        return totalRuns.toDouble() / dismissals.toDouble()
    }

    /**
     * Calculates batting strike rate: (Total runs / balls faced) * 100.
     * Returns null if balls faced == 0.
     */
    fun calculateBattingStrikeRate(runs: Int, ballsFaced: Int): Double? {
        if (ballsFaced <= 0) return null
        return (runs.toDouble() / ballsFaced.toDouble()) * 100.0
    }

    /**
     * Calculates boundary percentage: ((fours * 4 + sixes * 6) / totalRuns) * 100.
     * Returns null if total runs == 0.
     */
    fun calculateBoundaryPercentage(totalRuns: Int, fours: Int, sixes: Int): Double? {
        if (totalRuns <= 0) return null
        val boundaryRuns = (fours * 4) + (sixes * 6)
        return (boundaryRuns.toDouble() / totalRuns.toDouble()) * 100.0
    }

    /**
     * Calculates bowling economy rate: Runs conceded / (legal balls / 6.0).
     * Returns null if legal balls == 0.
     */
    fun calculateBowlingEconomy(runsConceded: Int, legalBalls: Int): Double? {
        if (legalBalls <= 0) return null
        val oversDouble = legalBalls.toDouble() / 6.0
        return runsConceded.toDouble() / oversDouble
    }

    /**
     * Calculates bowling average: Runs conceded / wickets taken.
     * Returns null if wickets == 0.
     */
    fun calculateBowlingAverage(runsConceded: Int, wickets: Int): Double? {
        if (wickets <= 0) return null
        return runsConceded.toDouble() / wickets.toDouble()
    }

    /**
     * Calculates bowling strike rate: Legal balls / wickets taken.
     * Returns null if wickets == 0.
     */
    fun calculateBowlingStrikeRate(legalBalls: Int, wickets: Int): Double? {
        if (wickets <= 0) return null
        return legalBalls.toDouble() / wickets.toDouble()
    }

    /**
     * Formats decimal statistics with graceful zero-division fallback ("--").
     * Never returns NaN or Infinity.
     */
    fun formatStat(value: Double?, decimals: Int = 2): String {
        if (value == null || value.isNaN() || value.isInfinite()) {
            return "--"
        }
        return String.format(Locale.US, "%.${decimals}f", value)
    }

    /**
     * Formats integer stat or fallback.
     */
    fun formatIntStat(value: Int?): String {
        return value?.toString() ?: "--"
    }
}
