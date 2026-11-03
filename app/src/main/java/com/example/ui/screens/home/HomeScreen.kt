package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CareerStatistics
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.engine.CricketStatsEngine
import com.example.ui.components.EmptyCricketPlaceholder
import com.example.ui.components.PlayerSelectorHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    players: List<Player>,
    selectedPlayer: Player?,
    matches: List<MatchRecord>,
    careerStats: CareerStatistics?,
    onSelectPlayer: (Player) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onNavigateToMatches: () -> Unit,
    onAddMatchClick: () -> Unit,
    onMatchClick: (MatchRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMatchClick,
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Match") },
                text = { Text("Add Match", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("home_add_match_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Player Selector Header
            item {
                PlayerSelectorHeader(
                    players = players,
                    selectedPlayer = selectedPlayer,
                    onSelectPlayer = onSelectPlayer,
                    onManagePlayers = onNavigateToPlayers
                )
            }

            if (selectedPlayer == null) {
                item {
                    EmptyCricketPlaceholder(
                        title = "No Player Profile",
                        message = "Create your first player profile to start tracking your cricket career.",
                        actionButton = {
                            Button(
                                onClick = onNavigateToPlayers,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Create Player Profile")
                            }
                        }
                    )
                }
            } else {
                // Key Career Highlights Row (4 Main Stats)
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Text(
                            text = "CAREER HIGHLIGHTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Matches",
                                value = (careerStats?.totalMatches ?: 0).toString(),
                                subtitle = "${careerStats?.battingInnings ?: 0} Innings",
                                modifier = Modifier.weight(1f),
                                accentColor = EmeraldPrimary
                            )
                            StatCard(
                                title = "Runs",
                                value = (careerStats?.totalRuns ?: 0).toString(),
                                subtitle = "HS: ${careerStats?.highestScoreDisplay ?: "--"}",
                                modifier = Modifier.weight(1f),
                                accentColor = CricketGold,
                                badgeText = "BAT"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(
                                title = "Wickets",
                                value = (careerStats?.totalWickets ?: 0).toString(),
                                subtitle = "BBI: ${careerStats?.bestBowlingDisplay ?: "--"}",
                                modifier = Modifier.weight(1f),
                                accentColor = CricketBallRed,
                                badgeText = "BOWL"
                            )
                            StatCard(
                                title = "Bat Avg",
                                value = CricketStatsEngine.formatStat(careerStats?.battingAverage),
                                subtitle = "SR: ${CricketStatsEngine.formatStat(careerStats?.battingStrikeRate)}",
                                modifier = Modifier.weight(1f),
                                accentColor = EmeraldTurf
                            )
                        }
                    }
                }

                // Batting Career Breakdown Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "BATTING CAREER",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(
                                title = "Strike Rate",
                                value = CricketStatsEngine.formatStat(careerStats?.battingStrikeRate),
                                subtitle = "${careerStats?.totalBallsFaced ?: 0} Balls Faced",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Not Outs",
                                value = (careerStats?.notOuts ?: 0).toString(),
                                subtitle = "${careerStats?.dismissals ?: 0} Dismissals",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(
                                title = "Boundaries",
                                value = "${careerStats?.totalFours ?: 0} x 4s • ${careerStats?.totalSixes ?: 0} x 6s",
                                subtitle = "Boundary: ${CricketStatsEngine.formatStat(careerStats?.boundaryPercentage)}%",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Milestones",
                                value = "${careerStats?.hundredsCount ?: 0} x 100s • ${careerStats?.fiftiesCount ?: 0} x 50s",
                                subtitle = "${careerStats?.ducksCount ?: 0} Ducks",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Bowling Career Breakdown Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "BOWLING CAREER",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(
                                title = "Overs Bowled",
                                value = careerStats?.totalOversString ?: "0.0",
                                subtitle = "${careerStats?.totalLegalBallsBowled ?: 0} Legal Balls",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Economy Rate",
                                value = CricketStatsEngine.formatStat(careerStats?.bowlingEconomy),
                                subtitle = "${careerStats?.totalRunsConceded ?: 0} Runs Conceded",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard(
                                title = "Bowl Avg",
                                value = CricketStatsEngine.formatStat(careerStats?.bowlingAverage),
                                subtitle = "SR: ${CricketStatsEngine.formatStat(careerStats?.bowlingStrikeRate)}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Hauls & Extras",
                                value = "3W: ${careerStats?.threeWicketHauls ?: 0} • 5W: ${careerStats?.fiveWicketHauls ?: 0}",
                                subtitle = "Wides: ${careerStats?.totalWides ?: 0} • NB: ${careerStats?.totalNoBalls ?: 0}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Fielding Summary Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "FIELDING SUMMARY",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = (careerStats?.totalCatches ?: 0).toString(),
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text("Catches", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = (careerStats?.totalRunOuts ?: 0).toString(),
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text("Run-Outs", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = (careerStats?.totalStumpings ?: 0).toString(),
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text("Stumpings", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                        }
                    }
                }

                // Recent Matches Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT MATCHES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        TextButton(onClick = onNavigateToMatches) {
                            Text("View All (${matches.size})", color = EmeraldPrimary)
                        }
                    }
                }

                if (matches.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "No matches recorded for this player yet. Tap '+ Add Match' below to enter your first match scorecard!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(matches.take(3)) { match ->
                        RecentMatchItem(match = match, onClick = { onMatchClick(match) })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun RecentMatchItem(
    match: MatchRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("recent_match_card_${match.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "vs ${match.opponentName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${match.matchDate} • ${match.matchFormat} (${match.matchType})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val resultColor = when (match.matchResult.lowercase()) {
                    "won" -> EmeraldPrimary
                    "lost" -> CricketBallRed
                    else -> CricketGold
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(resultColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = match.matchResult.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = resultColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (match.didBat) {
                    Column {
                        Text(
                            text = "BAT: ${match.runsScored} (${match.ballsFaced}b)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val sr = CricketStatsEngine.calculateBattingStrikeRate(match.runsScored, match.ballsFaced)
                        Text(
                            text = "SR: ${CricketStatsEngine.formatStat(sr)} • ${match.inningsEnded}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text("Did not bat", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }

                if (match.didBowl) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "BOWL: ${match.wicketsTaken}/${match.runsConceded}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val econ = CricketStatsEngine.calculateBowlingEconomy(match.runsConceded, match.legalBallsBowled)
                        Text(
                            text = "${match.oversBowledStr} ov • Econ: ${CricketStatsEngine.formatStat(econ)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text("Did not bowl", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }
    }
}
