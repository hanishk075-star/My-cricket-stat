package com.example.ui.screens.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.engine.CricketStatsEngine
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMatchDialog(
    matchToEdit: MatchRecord?,
    players: List<Player>,
    defaultPlayerId: String?,
    onDismiss: () -> Unit,
    onSave: (MatchRecord, isEdit: Boolean) -> Unit
) {
    val isEdit = matchToEdit != null
    val initialDate = remember {
        matchToEdit?.matchDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    var selectedPlayerId by remember {
        mutableStateOf(matchToEdit?.playerId ?: defaultPlayerId ?: players.firstOrNull()?.id ?: "")
    }
    var playerDropdownExpanded by remember { mutableStateOf(false) }

    var matchDate by remember { mutableStateOf(initialDate) }
    var opponentName by remember { mutableStateOf(matchToEdit?.opponentName ?: "") }
    var playerTeam by remember { mutableStateOf(matchToEdit?.playerTeam ?: "My Team") }
    var matchFormat by remember { mutableStateOf(matchToEdit?.matchFormat ?: "T20") }
    var formatDropdownExpanded by remember { mutableStateOf(false) }

    var venue by remember { mutableStateOf(matchToEdit?.venue ?: "") }
    var matchType by remember { mutableStateOf(matchToEdit?.matchType ?: "Club cricket") }
    var matchResult by remember { mutableStateOf(matchToEdit?.matchResult ?: "Won") }
    var scheduledOversStr by remember { mutableStateOf(matchToEdit?.scheduledOvers?.toString() ?: "20") }
    var matchNotes by remember { mutableStateOf(matchToEdit?.matchNotes ?: "") }

    // Batting
    var didBat by remember { mutableStateOf(matchToEdit?.didBat ?: true) }
    var runsScoredStr by remember { mutableStateOf(matchToEdit?.runsScored?.toString() ?: "0") }
    var ballsFacedStr by remember { mutableStateOf(matchToEdit?.ballsFaced?.toString() ?: "0") }
    var foursStr by remember { mutableStateOf(matchToEdit?.fours?.toString() ?: "0") }
    var sixesStr by remember { mutableStateOf(matchToEdit?.sixes?.toString() ?: "0") }
    var inningsEnded by remember { mutableStateOf(matchToEdit?.inningsEnded ?: "Not out") }
    var dismissalDropdownExpanded by remember { mutableStateOf(false) }
    var battingPositionStr by remember { mutableStateOf(matchToEdit?.battingPosition?.toString() ?: "3") }

    // Bowling
    var didBowl by remember { mutableStateOf(matchToEdit?.didBowl ?: false) }
    var oversBowledStr by remember { mutableStateOf(matchToEdit?.oversBowledStr ?: "0.0") }
    var maidensStr by remember { mutableStateOf(matchToEdit?.maidens?.toString() ?: "0") }
    var runsConcededStr by remember { mutableStateOf(matchToEdit?.runsConceded?.toString() ?: "0") }
    var wicketsTakenStr by remember { mutableStateOf(matchToEdit?.wicketsTaken?.toString() ?: "0") }
    var widesStr by remember { mutableStateOf(matchToEdit?.wides?.toString() ?: "0") }
    var noBallsStr by remember { mutableStateOf(matchToEdit?.noBalls?.toString() ?: "0") }
    var bowlingNotes by remember { mutableStateOf(matchToEdit?.bowlingNotes ?: "") }

    // Fielding
    var catchesStr by remember { mutableStateOf(matchToEdit?.catches?.toString() ?: "0") }
    var runOutsStr by remember { mutableStateOf(matchToEdit?.runOuts?.toString() ?: "0") }
    var stumpingsStr by remember { mutableStateOf(matchToEdit?.stumpings?.toString() ?: "0") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calculated previews
    val runsScored = runsScoredStr.toIntOrNull() ?: 0
    val ballsFaced = ballsFacedStr.toIntOrNull() ?: 0
    val calculatedSR = remember(runsScored, ballsFaced) {
        CricketStatsEngine.calculateBattingStrikeRate(runsScored, ballsFaced)
    }

    val legalBallsBowled = remember(oversBowledStr) {
        CricketStatsEngine.oversStringToLegalBalls(oversBowledStr)
    }
    val runsConceded = runsConcededStr.toIntOrNull() ?: 0
    val calculatedEconomy = remember(runsConceded, legalBallsBowled) {
        if (legalBallsBowled != null && legalBallsBowled > 0) {
            CricketStatsEngine.calculateBowlingEconomy(runsConceded, legalBallsBowled)
        } else null
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) "Edit Match Record" else "Add Match Record",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CricketBallRed.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = CricketBallRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Player Selector
                ExposedDropdownMenuBox(
                    expanded = playerDropdownExpanded,
                    onExpandedChange = { playerDropdownExpanded = it }
                ) {
                    val currentPlayerName = players.firstOrNull { it.id == selectedPlayerId }?.fullName ?: "Select Player"
                    OutlinedTextField(
                        value = currentPlayerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Player") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = playerDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("match_player_selector")
                    )
                    ExposedDropdownMenu(
                        expanded = playerDropdownExpanded,
                        onDismissRequest = { playerDropdownExpanded = false }
                    ) {
                        players.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("#${p.jerseyNumber} ${p.fullName}") },
                                onClick = {
                                    selectedPlayerId = p.id
                                    playerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Date & Format
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = matchDate,
                        onValueChange = { matchDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1.2f).testTag("match_date_input")
                    )

                    ExposedDropdownMenuBox(
                        expanded = formatDropdownExpanded,
                        onExpandedChange = { formatDropdownExpanded = it },
                        modifier = Modifier.weight(0.8f)
                    ) {
                        OutlinedTextField(
                            value = matchFormat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Format") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = formatDropdownExpanded,
                            onDismissRequest = { formatDropdownExpanded = false }
                        ) {
                            listOf("T5", "T10", "T20", "ODI", "Test", "Custom").forEach { fmt ->
                                DropdownMenuItem(
                                    text = { Text(fmt) },
                                    onClick = {
                                        matchFormat = fmt
                                        formatDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opponent & Team
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = opponentName,
                        onValueChange = { opponentName = it },
                        label = { Text("Opponent Name") },
                        modifier = Modifier.weight(1f).testTag("match_opponent_input")
                    )
                    OutlinedTextField(
                        value = playerTeam,
                        onValueChange = { playerTeam = it },
                        label = { Text("Your Team") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Venue & Match Result
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue (Optional)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = matchResult,
                        onValueChange = { matchResult = it },
                        label = { Text("Result (Won/Lost/Tie)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------- BATTING SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BATTING PERFORMANCE",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Batted", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = didBat,
                                    onCheckedChange = { didBat = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                                )
                            }
                        }

                        if (didBat) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = runsScoredStr,
                                    onValueChange = { runsScoredStr = it },
                                    label = { Text("Runs") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).testTag("batting_runs_input")
                                )
                                OutlinedTextField(
                                    value = ballsFacedStr,
                                    onValueChange = { ballsFacedStr = it },
                                    label = { Text("Balls Faced") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).testTag("batting_balls_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = foursStr,
                                    onValueChange = { foursStr = it },
                                    label = { Text("Fours (4s)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = sixesStr,
                                    onValueChange = { sixesStr = it },
                                    label = { Text("Sixes (6s)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            // Dismissal Dropdown
                            ExposedDropdownMenuBox(
                                expanded = dismissalDropdownExpanded,
                                onExpandedChange = { dismissalDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = inningsEnded,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("How Innings Ended") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dismissalDropdownExpanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = dismissalDropdownExpanded,
                                    onDismissRequest = { dismissalDropdownExpanded = false }
                                ) {
                                    listOf(
                                        "Not out", "Bowled", "Caught", "LBW", "Run out",
                                        "Stumped", "Hit wicket", "Retired out", "Other dismissal"
                                    ).forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(mode) },
                                            onClick = {
                                                inningsEnded = mode
                                                dismissalDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            // Calculated Batting Strike Rate Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Preview Strike Rate: ${CricketStatsEngine.formatStat(calculatedSR)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ---------------- BOWLING SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BOWLING PERFORMANCE",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Bowled", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = didBowl,
                                    onCheckedChange = { didBowl = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                                )
                            }
                        }

                        if (didBowl) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = oversBowledStr,
                                    onValueChange = { oversBowledStr = it },
                                    label = { Text("Overs (e.g. 4.2)") },
                                    supportingText = {
                                        if (legalBallsBowled == null) {
                                            Text("Invalid format (balls 0-5)", color = CricketBallRed)
                                        } else {
                                            Text("$legalBallsBowled legal balls", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    },
                                    isError = legalBallsBowled == null,
                                    modifier = Modifier.weight(1f).testTag("bowling_overs_input")
                                )
                                OutlinedTextField(
                                    value = maidensStr,
                                    onValueChange = { maidensStr = it },
                                    label = { Text("Maidens") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = runsConcededStr,
                                    onValueChange = { runsConcededStr = it },
                                    label = { Text("Runs Conceded") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).testTag("bowling_runs_conceded_input")
                                )
                                OutlinedTextField(
                                    value = wicketsTakenStr,
                                    onValueChange = { wicketsTakenStr = it },
                                    label = { Text("Wickets Taken") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f).testTag("bowling_wickets_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = widesStr,
                                    onValueChange = { widesStr = it },
                                    label = { Text("Wides") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = noBallsStr,
                                    onValueChange = { noBallsStr = it },
                                    label = { Text("No-Balls") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            // Calculated Bowling Economy Preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Preview Economy: ${CricketStatsEngine.formatStat(calculatedEconomy)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ---------------- FIELDING SECTION ----------------
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "FIELDING PERFORMANCE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = catchesStr,
                                onValueChange = { catchesStr = it },
                                label = { Text("Catches") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = runOutsStr,
                                onValueChange = { runOutsStr = it },
                                label = { Text("Run-outs") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = stumpingsStr,
                                onValueChange = { stumpingsStr = it },
                                label = { Text("Stumpings") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = matchNotes,
                    onValueChange = { matchNotes = it },
                    label = { Text("Short Match Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // Validations
                            if (selectedPlayerId.isBlank()) {
                                errorMessage = "Please select a player."
                                return@Button
                            }
                            if (opponentName.isBlank()) {
                                errorMessage = "Please enter the opponent name."
                                return@Button
                            }
                            if (matchDate.isBlank()) {
                                errorMessage = "Please enter match date."
                                return@Button
                            }
                            if (didBowl && legalBallsBowled == null) {
                                errorMessage = "Invalid overs notation. Second digit must be 0 through 5 (e.g. 4.2)."
                                return@Button
                            }

                            val match = MatchRecord(
                                id = matchToEdit?.id ?: "match_${UUID.randomUUID()}",
                                playerId = selectedPlayerId,
                                matchDate = matchDate.trim(),
                                opponentName = opponentName.trim(),
                                playerTeam = playerTeam.trim(),
                                matchFormat = matchFormat,
                                venue = venue.trim(),
                                matchType = matchType,
                                scheduledOvers = scheduledOversStr.toIntOrNull(),
                                matchResult = matchResult,
                                matchNotes = matchNotes.trim(),
                                didBat = didBat,
                                runsScored = runsScored,
                                ballsFaced = ballsFaced,
                                fours = foursStr.toIntOrNull() ?: 0,
                                sixes = sixesStr.toIntOrNull() ?: 0,
                                inningsEnded = inningsEnded,
                                battingPosition = battingPositionStr.toIntOrNull(),
                                didBowl = didBowl,
                                oversBowledStr = oversBowledStr.trim(),
                                legalBallsBowled = legalBallsBowled ?: 0,
                                maidens = maidensStr.toIntOrNull() ?: 0,
                                runsConceded = runsConceded,
                                wicketsTaken = wicketsTakenStr.toIntOrNull() ?: 0,
                                wides = widesStr.toIntOrNull() ?: 0,
                                noBalls = noBallsStr.toIntOrNull() ?: 0,
                                bowlingNotes = bowlingNotes.trim(),
                                catches = catchesStr.toIntOrNull() ?: 0,
                                runOuts = runOutsStr.toIntOrNull() ?: 0,
                                stumpings = stumpingsStr.toIntOrNull() ?: 0,
                                createdAt = matchToEdit?.createdAt ?: System.currentTimeMillis()
                            )
                            onSave(match, isEdit)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("save_match_button")
                    ) {
                        Text(if (isEdit) "Update Match" else "Save Match")
                    }
                }
            }
        }
    }
}
