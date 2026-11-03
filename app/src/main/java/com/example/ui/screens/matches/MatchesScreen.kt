package com.example.ui.screens.matches

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.engine.CricketStatsEngine
import com.example.ui.components.EmptyCricketPlaceholder
import com.example.ui.components.PlayerSelectorHeader
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.TextMuted

@Composable
fun MatchesScreen(
    players: List<Player>,
    selectedPlayer: Player?,
    matches: List<MatchRecord>,
    onSelectPlayer: (Player) -> Unit,
    onNavigateToPlayers: () -> Unit,
    onSaveMatch: (MatchRecord, isEdit: Boolean) -> Unit,
    onDeleteMatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFormatFilter by remember { mutableStateOf("All") }
    var sortDescending by remember { mutableStateOf(true) }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<MatchRecord?>(null) }
    var matchDetailToShow by remember { mutableStateOf<MatchRecord?>(null) }
    var matchToDelete by remember { mutableStateOf<MatchRecord?>(null) }

    val formatFilters = listOf("All", "T20", "ODI", "Test", "T10", "Custom")

    val filteredMatches = remember(matches, searchQuery, selectedFormatFilter, sortDescending) {
        matches.filter { m ->
            val formatMatch = if (selectedFormatFilter == "All") true else m.matchFormat.equals(selectedFormatFilter, ignoreCase = true)
            val queryMatch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                m.opponentName.lowercase().contains(q) ||
                        m.venue.lowercase().contains(q) ||
                        m.matchNotes.lowercase().contains(q) ||
                        m.playerTeam.lowercase().contains(q)
            }
            formatMatch && queryMatch
        }.sortedWith { a, b ->
            if (sortDescending) b.matchDate.compareTo(a.matchDate)
            else a.matchDate.compareTo(b.matchDate)
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    matchToEdit = null
                    showAddEditDialog = true
                },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Match") },
                text = { Text("Add Match", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("matches_add_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                PlayerSelectorHeader(
                    players = players,
                    selectedPlayer = selectedPlayer,
                    onSelectPlayer = onSelectPlayer,
                    onManagePlayers = onNavigateToPlayers
                )
            }

            // Search Bar & Sort Toggle
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search matches (Opponent, Venue)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("match_search_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { sortDescending = !sortDescending },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort Direction",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Format Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(formatFilters) { fmt ->
                            FilterChip(
                                selected = selectedFormatFilter == fmt,
                                onClick = { selectedFormatFilter = fmt },
                                label = { Text(fmt) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Match list header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MATCH HISTORY (${filteredMatches.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (sortDescending) "Newest First" else "Oldest First",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredMatches.isEmpty()) {
                item {
                    EmptyCricketPlaceholder(
                        title = if (matches.isEmpty()) "No Matches Logged" else "No Matches Match Filter",
                        message = if (matches.isEmpty())
                            "Add your post-match batting and bowling performances to build your career record."
                        else "Try clearing your search term or format filter.",
                        actionButton = if (matches.isEmpty()) {
                            {
                                Button(
                                    onClick = {
                                        matchToEdit = null
                                        showAddEditDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                ) {
                                    Text("Add Match Scorecard")
                                }
                            }
                        } else null
                    )
                }
            } else {
                items(filteredMatches) { match ->
                    MatchCardItem(
                        match = match,
                        onClick = { matchDetailToShow = match },
                        onEdit = {
                            matchToEdit = match
                            showAddEditDialog = true
                        },
                        onDelete = {
                            matchToDelete = match
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddEditDialog) {
        AddEditMatchDialog(
            matchToEdit = matchToEdit,
            players = players,
            defaultPlayerId = selectedPlayer?.id,
            onDismiss = { showAddEditDialog = false },
            onSave = { match, isEdit ->
                onSaveMatch(match, isEdit)
                showAddEditDialog = false
            }
        )
    }

    if (matchDetailToShow != null) {
        MatchDetailDialog(
            match = matchDetailToShow!!,
            onDismiss = { matchDetailToShow = null },
            onEdit = {
                matchToEdit = matchDetailToShow
                matchDetailToShow = null
                showAddEditDialog = true
            }
        )
    }

    if (matchToDelete != null) {
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text("Delete Match Record") },
            text = {
                Text("Are you sure you want to delete the match vs '${matchToDelete?.opponentName}' on ${matchToDelete?.matchDate}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        matchToDelete?.let { onDeleteMatch(it.id) }
                        matchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketBallRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MatchCardItem(
    match: MatchRecord,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Opponent, Date, Result Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "vs ${match.opponentName}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${match.matchDate} • ${match.matchFormat} (${match.matchType})${if (match.venue.isNotBlank()) " @ ${match.venue}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
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
                        .background(resultColor.copy(alpha = 0.18f))
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

            // Batting & Bowling side-by-side stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (match.didBat) {
                        Text(
                            text = "Bat: ${match.runsScored} (${match.ballsFaced} balls)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val sr = CricketStatsEngine.calculateBattingStrikeRate(match.runsScored, match.ballsFaced)
                        Text(
                            text = "SR: ${CricketStatsEngine.formatStat(sr)} • ${match.fours}x4, ${match.sixes}x6 • ${match.inningsEnded}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text("Did not bat", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    if (match.didBowl) {
                        Text(
                            text = "Bowl: ${match.wicketsTaken}/${match.runsConceded}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val econ = CricketStatsEngine.calculateBowlingEconomy(match.runsConceded, match.legalBallsBowled)
                        Text(
                            text = "${match.oversBowledStr} ov • Econ: ${CricketStatsEngine.formatStat(econ)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text("Did not bowl", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }

            // Quick Actions Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CricketBallRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun MatchDetailDialog(
    match: MatchRecord,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Match Scorecard",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${match.playerTeam} vs ${match.opponentName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${match.matchDate} • ${match.matchFormat} • ${match.matchResult}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (match.venue.isNotBlank()) {
                    Text(
                        text = "Venue: ${match.venue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (match.didBat) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "BATTING",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${match.runsScored} runs off ${match.ballsFaced} balls",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            val sr = CricketStatsEngine.calculateBattingStrikeRate(match.runsScored, match.ballsFaced)
                            Text(
                                text = "Strike Rate: ${CricketStatsEngine.formatStat(sr)} | Boundaries: ${match.fours}x4, ${match.sixes}x6",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Dismissal: ${match.inningsEnded}${match.battingPosition?.let { " (Pos #$it)" } ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (match.didBowl) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "BOWLING",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${match.wicketsTaken} wickets for ${match.runsConceded} runs (${match.oversBowledStr} overs)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            val econ = CricketStatsEngine.calculateBowlingEconomy(match.runsConceded, match.legalBallsBowled)
                            Text(
                                text = "Economy: ${CricketStatsEngine.formatStat(econ)} | Maidens: ${match.maidens}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Extras: ${match.wides} wides, ${match.noBalls} no-balls",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (match.catches > 0 || match.runOuts > 0 || match.stumpings > 0) {
                    Text(
                        text = "Fielding: ${match.catches} Catches, ${match.runOuts} Run-outs, ${match.stumpings} Stumpings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (match.matchNotes.isNotBlank()) {
                    Text(
                        text = "Notes: ${match.matchNotes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Edit Scorecard")
                    }
                }
            }
        }
    }
}
