package com.example.ui.screens.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CareerStatistics
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.engine.CricketStatsEngine
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    selectedPlayer: Player?,
    matches: List<MatchRecord>,
    careerStats: CareerStatistics?,
    onExportBackup: suspend () -> String,
    onImportBackup: suspend (String, Boolean) -> Result<Int>,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJson by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "APPLICATION SETTINGS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Appearance
        item {
            SettingSectionHeader("Appearance")
            SettingRow(
                icon = Icons.Default.Brightness4,
                title = "Dark Theme",
                subtitle = "Toggle dark emerald green cricket theme",
                trailing = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Data Backup & Restore
        item {
            SettingSectionHeader("Data Backup & Restore")

            SettingRow(
                icon = Icons.Default.Download,
                title = "Export JSON Backup",
                subtitle = "Save all player profiles and matches to a JSON file or clipboard",
                onClick = {
                    coroutineScope.launch {
                        exportedJson = onExportBackup()
                        showExportDialog = true
                    }
                }
            )

            SettingRow(
                icon = Icons.Default.Upload,
                title = "Import JSON Backup",
                subtitle = "Restore player profiles and matches with merge option",
                onClick = { showImportDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Career Reports & Sharing
        item {
            SettingSectionHeader("Career Report & Sharing")

            SettingRow(
                icon = Icons.Default.PictureAsPdf,
                title = "Generate Career Report",
                subtitle = if (selectedPlayer != null) "Export summary for ${selectedPlayer.fullName}" else "Select a player first",
                onClick = {
                    if (selectedPlayer != null) {
                        showReportDialog = true
                    } else {
                        Toast.makeText(context, "Please select an active player first", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // About & Offline Privacy
        item {
            SettingSectionHeader("About & Privacy")

            SettingRow(
                icon = Icons.Default.Info,
                title = "Offline-First Privacy",
                subtitle = "All statistics, profiles, and match histories are stored 100% locally on your device with no trackers.",
                onClick = {}
            )

            SettingRow(
                icon = Icons.Default.DeleteForever,
                title = "Clear All Records",
                subtitle = "Delete all players, match scorecards, and quiz records",
                titleColor = CricketBallRed,
                onClick = { showClearConfirmDialog = true }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Data Backup (JSON)",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Copy or share this complete JSON backup to restore your records on any device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = exportedJson,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showExportDialog = false }) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MyCricketStats Backup", exportedJson)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied backup to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Copy")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, exportedJson)
                                    type = "application/json"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Backup JSON")
                                context.startActivity(shareIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share")
                        }
                    }
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        var importInput by remember { mutableStateOf("") }
        var mergeOption by remember { mutableStateOf(true) }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { showImportDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Restore Records",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Paste your previously exported JSON backup below to restore player profiles and match scorecards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (errorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = errorMsg ?: "", color = CricketBallRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = importInput,
                        onValueChange = { importInput = it },
                        label = { Text("Paste JSON Backup") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 8
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (mergeOption) "Merge with existing data" else "Overwrite existing data",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Switch(
                            checked = mergeOption,
                            onCheckedChange = { mergeOption = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showImportDialog = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (importInput.isBlank()) {
                                    errorMsg = "Please paste JSON backup data."
                                    return@Button
                                }
                                coroutineScope.launch {
                                    val result = onImportBackup(importInput, mergeOption)
                                    if (result.isSuccess) {
                                        Toast.makeText(context, "Successfully restored ${result.getOrNull()} records!", Toast.LENGTH_LONG).show()
                                        showImportDialog = false
                                    } else {
                                        errorMsg = "Invalid JSON structure. Please verify backup content."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }

    // Career Report Dialog
    if (showReportDialog && selectedPlayer != null && careerStats != null) {
        val reportText = remember(selectedPlayer, careerStats, matches) {
            buildString {
                appendLine("=========================================")
                appendLine("MYCRICKETSTATS - OFFICIAL CAREER REPORT")
                appendLine("=========================================")
                appendLine("Player: ${selectedPlayer.fullName} (#${selectedPlayer.jerseyNumber})")
                appendLine("Role: ${selectedPlayer.playingRole} | Team: ${selectedPlayer.favouriteTeam}")
                appendLine("Batting Hand: ${selectedPlayer.battingHand} | Bowling Arm: ${selectedPlayer.bowlingArm}")
                appendLine("Bowling Style: ${selectedPlayer.bowlingStyle}")
                appendLine("-----------------------------------------")
                appendLine("CAREER TOTALS (${careerStats.totalMatches} Matches)")
                appendLine("-----------------------------------------")
                appendLine("BATTING:")
                appendLine("  Innings: ${careerStats.battingInnings} | Not Outs: ${careerStats.notOuts}")
                appendLine("  Runs: ${careerStats.totalRuns} | High Score: ${careerStats.highestScoreDisplay}")
                appendLine("  Average: ${CricketStatsEngine.formatStat(careerStats.battingAverage)}")
                appendLine("  Strike Rate: ${CricketStatsEngine.formatStat(careerStats.battingStrikeRate)}")
                appendLine("  Boundaries: ${careerStats.totalFours} fours, ${careerStats.totalSixes} sixes")
                appendLine("  Boundary %: ${CricketStatsEngine.formatStat(careerStats.boundaryPercentage)}%")
                appendLine("  Milestones: ${careerStats.hundredsCount} 100s, ${careerStats.fiftiesCount} 50s, ${careerStats.ducksCount} Ducks")
                appendLine("BOWLING:")
                appendLine("  Overs: ${careerStats.totalOversString} (${careerStats.totalLegalBallsBowled} legal balls)")
                appendLine("  Wickets: ${careerStats.totalWickets} | Runs: ${careerStats.totalRunsConceded}")
                appendLine("  Best Bowling: ${careerStats.bestBowlingDisplay}")
                appendLine("  Economy: ${CricketStatsEngine.formatStat(careerStats.bowlingEconomy)}")
                appendLine("  Bowling Avg: ${CricketStatsEngine.formatStat(careerStats.bowlingAverage)}")
                appendLine("  Bowling SR: ${CricketStatsEngine.formatStat(careerStats.bowlingStrikeRate)}")
                appendLine("  Maidens: ${careerStats.totalMaidens} | Hauls: 3W x ${careerStats.threeWicketHauls}, 5W x ${careerStats.fiveWicketHauls}")
                appendLine("FIELDING:")
                appendLine("  Catches: ${careerStats.totalCatches} | Run-Outs: ${careerStats.totalRunOuts} | Stumpings: ${careerStats.totalStumpings}")
                appendLine("-----------------------------------------")
                appendLine("RECENT MATCHES:")
                matches.take(5).forEach { m ->
                    appendLine("• ${m.matchDate} vs ${m.opponentName} (${m.matchFormat} - ${m.matchResult}): Bat: ${m.runsScored}(${m.ballsFaced}), Bowl: ${m.wicketsTaken}/${m.runsConceded} (${m.oversBowledStr} ov)")
                }
                appendLine("=========================================")
                appendLine("Generated by MyCricketStats")
            }
        }

        Dialog(
            onDismissRequest = { showReportDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Player Career Report",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = reportText,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showReportDialog = false }) {
                            Text("Close")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Career Report")
                                context.startActivity(shareIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Report")
                        }
                    }
                }
            }
        }
    }

    // Confirm Clear All Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All Application Data?") },
            text = {
                Text("This action will permanently delete all player profiles, all match histories, and all quiz records from your device. This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "All data has been cleared.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketBallRed)
                ) {
                    Text("Permanently Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (titleColor == CricketBallRed) CricketBallRed else EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = titleColor
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailing != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailing()
            }
        }
    }
}
