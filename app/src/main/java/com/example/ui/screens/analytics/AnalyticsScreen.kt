package com.example.ui.screens.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerStatistics
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.engine.CricketStatsEngine
import com.example.ui.components.EmptyCricketPlaceholder
import com.example.ui.components.PlayerSelectorHeader
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.SimpleTrendLineChart
import com.example.ui.components.StatCard
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf

@Composable
fun AnalyticsScreen(
    players: List<Player>,
    selectedPlayer: Player?,
    matches: List<MatchRecord>,
    careerStats: CareerStatistics?,
    onSelectPlayer: (Player) -> Unit,
    onNavigateToPlayers: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sort matches chronologically for trend analysis
    val chronologicalMatches = remember(matches) {
        matches.sortedBy { it.matchDate }
    }

    // Runs data points
    val runsDataPoints = remember(chronologicalMatches) {
        chronologicalMatches.filter { it.didBat }.map {
            val dateLabel = if (it.matchDate.length >= 10) it.matchDate.substring(5) else it.matchDate
            Pair(dateLabel, it.runsScored)
        }
    }

    // Wickets data points
    val wicketsDataPoints = remember(chronologicalMatches) {
        chronologicalMatches.filter { it.didBowl }.map {
            val dateLabel = if (it.matchDate.length >= 10) it.matchDate.substring(5) else it.matchDate
            Pair(dateLabel, it.wicketsTaken)
        }
    }

    // Strike rate trend points
    val strikeRatePoints = remember(chronologicalMatches) {
        chronologicalMatches.filter { it.didBat && it.ballsFaced > 0 }.map {
            val dateLabel = if (it.matchDate.length >= 10) it.matchDate.substring(5) else it.matchDate
            val sr = CricketStatsEngine.calculateBattingStrikeRate(it.runsScored, it.ballsFaced) ?: 0.0
            Pair(dateLabel, sr)
        }
    }

    // Bowling economy trend points
    val economyPoints = remember(chronologicalMatches) {
        chronologicalMatches.filter { it.didBowl && it.legalBallsBowled > 0 }.map {
            val dateLabel = if (it.matchDate.length >= 10) it.matchDate.substring(5) else it.matchDate
            val econ = CricketStatsEngine.calculateBowlingEconomy(it.runsConceded, it.legalBallsBowled) ?: 0.0
            Pair(dateLabel, econ)
        }
    }

    // Monthly match frequency distribution
    val monthlyDistribution = remember(matches) {
        val monthCountMap = mutableMapOf<String, Int>()
        matches.forEach { m ->
            val monthKey = if (m.matchDate.length >= 7) m.matchDate.substring(0, 7) else "Unknown"
            monthCountMap[monthKey] = (monthCountMap[monthKey] ?: 0) + 1
        }
        monthCountMap.entries.sortedBy { it.key }.map { Pair(it.key.substring(2), it.value) }
    }

    // Best Performances
    val bestBattingMatch = remember(matches) {
        matches.filter { it.didBat }.maxByOrNull { it.runsScored }
    }
    val bestBowlingMatch = remember(matches) {
        matches.filter { it.didBowl }.maxWithOrNull(
            compareBy<MatchRecord> { it.wicketsTaken }.thenByDescending { -it.runsConceded }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            PlayerSelectorHeader(
                players = players,
                selectedPlayer = selectedPlayer,
                onSelectPlayer = onSelectPlayer,
                onManagePlayers = onNavigateToPlayers
            )
        }

        if (matches.isEmpty()) {
            item {
                EmptyCricketPlaceholder(
                    title = "No Match Analytics Available",
                    message = "Once you record matches, career graphs and statistical breakdowns will dynamically appear here."
                )
            }
        } else {
            // Milestone Summary Cards
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        text = "CAREER MILESTONES & RECORDS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(
                            title = "Centuries (100s)",
                            value = (careerStats?.hundredsCount ?: 0).toString(),
                            subtitle = "${careerStats?.fiftiesCount ?: 0} Fifties (50s)",
                            modifier = Modifier.weight(1f),
                            accentColor = CricketGold
                        )
                        StatCard(
                            title = "Best Bowling",
                            value = careerStats?.bestBowlingDisplay ?: "--",
                            subtitle = "${careerStats?.threeWicketHauls ?: 0} x 3W • ${careerStats?.fiveWicketHauls ?: 0} x 5W",
                            modifier = Modifier.weight(1f),
                            accentColor = CricketBallRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(
                            title = "High Score",
                            value = careerStats?.highestScoreDisplay ?: "--",
                            subtitle = bestBattingMatch?.let { "vs ${it.opponentName}" } ?: "--",
                            modifier = Modifier.weight(1f),
                            accentColor = EmeraldPrimary
                        )
                        StatCard(
                            title = "Bowling Avg",
                            value = CricketStatsEngine.formatStat(careerStats?.bowlingAverage),
                            subtitle = "Econ: ${CricketStatsEngine.formatStat(careerStats?.bowlingEconomy)}",
                            modifier = Modifier.weight(1f),
                            accentColor = EmeraldTurf
                        )
                    }
                }
            }

            // Runs Scored Over Time Chart
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RUNS SCORED PER INNINGS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Chronological innings progression",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SimpleBarChart(
                            dataPoints = runsDataPoints,
                            barColor = EmeraldPrimary,
                            label = "Runs Scored"
                        )
                    }
                }
            }

            // Batting Strike Rate Trend Line Chart
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BATTING STRIKE RATE TREND",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pace of scoring across matches (target > 130 in T20)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SimpleTrendLineChart(
                            dataPoints = strikeRatePoints,
                            lineColor = CricketGold,
                            title = "Strike Rate (Runs / 100 Balls)"
                        )
                    }
                }
            }

            // Wickets Taken Per Match Chart
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "WICKETS TAKEN PER SPELL",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Bowling strike power across matches",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SimpleBarChart(
                            dataPoints = wicketsDataPoints,
                            barColor = CricketBallRed,
                            label = "Wickets"
                        )
                    }
                }
            }

            // Bowling Economy Trend Line Chart
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BOWLING ECONOMY RATE TREND",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Average runs conceded per over (lower is better)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SimpleTrendLineChart(
                            dataPoints = economyPoints,
                            lineColor = EmeraldTurf,
                            title = "Economy Rate (Runs / Over)"
                        )
                    }
                }
            }

            // Matches Per Month Distribution
            if (monthlyDistribution.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "MATCHES PLAYED PER MONTH",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SimpleBarChart(
                                dataPoints = monthlyDistribution,
                                barColor = MaterialTheme.colorScheme.primary,
                                label = "Matches"
                            )
                        }
                    }
                }
            }

            // Best Batting & Bowling Performance Highlights
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "CAREER PEAK PERFORMANCES",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (bestBattingMatch != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Top Batting Innings",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CricketGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${bestBattingMatch.runsScored} runs off ${bestBattingMatch.ballsFaced} balls (${bestBattingMatch.inningsEnded})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "vs ${bestBattingMatch.opponentName} on ${bestBattingMatch.matchDate} • ${bestBattingMatch.fours} fours, ${bestBattingMatch.sixes} sixes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (bestBowlingMatch != null && bestBowlingMatch.wicketsTaken > 0) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Top Bowling Spell",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CricketBallRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${bestBowlingMatch.wicketsTaken} wickets for ${bestBowlingMatch.runsConceded} runs (${bestBowlingMatch.oversBowledStr} overs)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "vs ${bestBowlingMatch.opponentName} on ${bestBowlingMatch.matchDate} • ${bestBowlingMatch.maidens} maidens",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
