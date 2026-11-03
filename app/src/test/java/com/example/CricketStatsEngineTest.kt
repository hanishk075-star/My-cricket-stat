package com.example

import com.example.engine.CricketStatsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CricketStatsEngineTest {

    @Test
    fun testOversConversionToLegalBalls() {
        assertEquals(24, CricketStatsEngine.oversStringToLegalBalls("4.0"))
        assertEquals(25, CricketStatsEngine.oversStringToLegalBalls("4.1"))
        assertEquals(26, CricketStatsEngine.oversStringToLegalBalls("4.2"))
        assertEquals(29, CricketStatsEngine.oversStringToLegalBalls("4.5"))
        assertEquals(30, CricketStatsEngine.oversStringToLegalBalls("5.0"))
        assertEquals(24, CricketStatsEngine.oversStringToLegalBalls("4"))
        assertEquals(0, CricketStatsEngine.oversStringToLegalBalls("0.0"))
        assertEquals(0, CricketStatsEngine.oversStringToLegalBalls(""))
        assertEquals(0, CricketStatsEngine.oversStringToLegalBalls(null))
    }

    @Test
    fun testInvalidOversNotationRejected() {
        // Cricket over cannot have 6 or more balls after dot (4.6 or 4.7 are invalid)
        assertNull(CricketStatsEngine.oversStringToLegalBalls("4.6"))
        assertNull(CricketStatsEngine.oversStringToLegalBalls("4.7"))
        assertNull(CricketStatsEngine.oversStringToLegalBalls("4.9"))
        assertNull(CricketStatsEngine.oversStringToLegalBalls("-1.2"))
        assertNull(CricketStatsEngine.oversStringToLegalBalls("abc"))
    }

    @Test
    fun testLegalBallsToOversString() {
        assertEquals("4.2", CricketStatsEngine.legalBallsToOversString(26))
        assertEquals("4.0", CricketStatsEngine.legalBallsToOversString(24))
        assertEquals("0.5", CricketStatsEngine.legalBallsToOversString(5))
        assertEquals("0.0", CricketStatsEngine.legalBallsToOversString(0))
    }

    @Test
    fun testUserPromptExampleBattingRecord() {
        // Runs: 45, Balls: 30, Dismissal: Caught, Fours: 5, Sixes: 2
        // Expected strike rate: 150.00
        val strikeRate = CricketStatsEngine.calculateBattingStrikeRate(45, 30)
        assertEquals(150.00, strikeRate!!, 0.001)
        assertEquals("150.00", CricketStatsEngine.formatStat(strikeRate))

        val avg = CricketStatsEngine.calculateBattingAverage(45, 1)
        assertEquals(45.00, avg!!, 0.001)

        val boundaryPct = CricketStatsEngine.calculateBoundaryPercentage(45, 5, 2)
        // 5*4 + 2*6 = 20 + 12 = 32 runs from boundaries. (32 / 45) * 100 = 71.11%
        assertEquals(71.11, boundaryPct!!, 0.01)
    }

    @Test
    fun testUserPromptExampleBowlingRecord() {
        // Overs: 4.2, Runs conceded: 26, Wickets: 2
        // Expected: Legal balls: 26, Economy: 6.00, Bowling average: 13.00, Bowling strike rate: 13.00
        val legalBalls = CricketStatsEngine.oversStringToLegalBalls("4.2")
        assertEquals(26, legalBalls)

        val economy = CricketStatsEngine.calculateBowlingEconomy(26, legalBalls!!)
        assertEquals(6.00, economy!!, 0.001)
        assertEquals("6.00", CricketStatsEngine.formatStat(economy))

        val bowlingAvg = CricketStatsEngine.calculateBowlingAverage(26, 2)
        assertEquals(13.00, bowlingAvg!!, 0.001)
        assertEquals("13.00", CricketStatsEngine.formatStat(bowlingAvg))

        val bowlingSR = CricketStatsEngine.calculateBowlingStrikeRate(26, 2)
        assertEquals(13.00, bowlingSR!!, 0.001)
        assertEquals("13.00", CricketStatsEngine.formatStat(bowlingSR))
    }

    @Test
    fun testZeroDivisionHandling() {
        // Zero dismissals -> batting average is null -> formatted as "--"
        val batAvgZeroDismissals = CricketStatsEngine.calculateBattingAverage(100, 0)
        assertNull(batAvgZeroDismissals)
        assertEquals("--", CricketStatsEngine.formatStat(batAvgZeroDismissals))

        // Zero balls faced -> batting strike rate is null -> formatted as "--"
        val srZeroBalls = CricketStatsEngine.calculateBattingStrikeRate(0, 0)
        assertNull(srZeroBalls)
        assertEquals("--", CricketStatsEngine.formatStat(srZeroBalls))

        // Zero legal deliveries -> bowling economy is null -> formatted as "--"
        val econZeroDeliveries = CricketStatsEngine.calculateBowlingEconomy(0, 0)
        assertNull(econZeroDeliveries)
        assertEquals("--", CricketStatsEngine.formatStat(econZeroDeliveries))

        // Zero wickets -> bowling average and strike rate are null -> formatted as "--"
        val bowlAvgZeroWickets = CricketStatsEngine.calculateBowlingAverage(25, 0)
        assertNull(bowlAvgZeroWickets)
        assertEquals("--", CricketStatsEngine.formatStat(bowlAvgZeroWickets))

        val bowlSRZeroWickets = CricketStatsEngine.calculateBowlingStrikeRate(24, 0)
        assertNull(bowlSRZeroWickets)
        assertEquals("--", CricketStatsEngine.formatStat(bowlSRZeroWickets))
    }

    @Test
    fun testDismissalClassification() {
        // Not out should NOT count as a dismissal
        assertFalse(CricketStatsEngine.isDismissal("Not out"))
        assertFalse(CricketStatsEngine.isDismissal("not out"))
        assertFalse(CricketStatsEngine.isDismissal("Retired not out"))

        // Standard dismissals
        assertTrue(CricketStatsEngine.isDismissal("Caught"))
        assertTrue(CricketStatsEngine.isDismissal("Bowled"))
        assertTrue(CricketStatsEngine.isDismissal("LBW"))
        assertTrue(CricketStatsEngine.isDismissal("Run out"))
        assertTrue(CricketStatsEngine.isDismissal("Stumped"))
        assertTrue(CricketStatsEngine.isDismissal("Hit wicket"))
    }

    @Test
    fun testBowlerCreditedWickets() {
        // Run out does NOT credit a wicket to the bowler
        assertFalse(CricketStatsEngine.isBowlerCreditedDismissal("Run out"))
        assertFalse(CricketStatsEngine.isBowlerCreditedDismissal("Obstructing the field"))
        assertFalse(CricketStatsEngine.isBowlerCreditedDismissal("Retired out"))

        // Credited modes
        assertTrue(CricketStatsEngine.isBowlerCreditedDismissal("Bowled"))
        assertTrue(CricketStatsEngine.isBowlerCreditedDismissal("Caught"))
        assertTrue(CricketStatsEngine.isBowlerCreditedDismissal("LBW"))
        assertTrue(CricketStatsEngine.isBowlerCreditedDismissal("Stumped"))
        assertTrue(CricketStatsEngine.isBowlerCreditedDismissal("Hit wicket"))
    }
}
