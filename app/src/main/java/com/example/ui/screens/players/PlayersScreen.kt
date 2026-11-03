package com.example.ui.screens.players

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Player
import com.example.ui.components.EmptyCricketPlaceholder
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun PlayersScreen(
    players: List<Player>,
    selectedPlayer: Player?,
    onSelectPlayer: (Player) -> Unit,
    onSavePlayer: (
        id: String?,
        fullName: String,
        jerseyNumber: Int,
        dateOfBirth: String,
        battingHand: String,
        bowlingArm: String,
        bowlingStyle: String,
        playingRole: String,
        favouriteTeam: String,
        description: String,
        avatarColorHex: String
    ) -> Unit,
    onDeletePlayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }
    var playerToDelete by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    playerToEdit = null
                    showAddEditDialog = true
                },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Player") },
                text = { Text("New Player", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_player_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "CRICKET PLAYER PROFILES",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "Manage multiple player careers. Each profile maintains isolated match records, scores, and bowling figures.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (players.isEmpty()) {
                item {
                    EmptyCricketPlaceholder(
                        title = "No Players Yet",
                        message = "Create your first player profile to track career statistics and matches.",
                        actionButton = {
                            Button(
                                onClick = {
                                    playerToEdit = null
                                    showAddEditDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Add Player Profile")
                            }
                        }
                    )
                }
            } else {
                items(players) { player ->
                    PlayerCard(
                        player = player,
                        isSelected = player.id == selectedPlayer?.id,
                        onSelect = { onSelectPlayer(player) },
                        onEdit = {
                            playerToEdit = player
                            showAddEditDialog = true
                        },
                        onDelete = {
                            playerToDelete = player
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddEditDialog) {
        AddEditPlayerDialog(
            player = playerToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { id, fullName, jerseyNumber, dob, battingHand, bowlingArm, bowlingStyle, role, favTeam, desc, colorHex ->
                onSavePlayer(id, fullName, jerseyNumber, dob, battingHand, bowlingArm, bowlingStyle, role, favTeam, desc, colorHex)
                showAddEditDialog = false
            }
        )
    }

    if (playerToDelete != null) {
        AlertDialog(
            onDismissRequest = { playerToDelete = null },
            title = { Text("Delete Player Profile") },
            text = {
                Text("Are you sure you want to delete '${playerToDelete?.fullName}'? All match records and career statistics associated with this player will be permanently deleted.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        playerToDelete?.let { onDeletePlayer(it.id) }
                        playerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricketBallRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { playerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlayerCard(
    player: Player,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_card_${player.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar badge
                    val parsedColor = try {
                        Color(android.graphics.Color.parseColor(player.avatarColorHex))
                    } catch (_: Exception) {
                        EmeraldTurf
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(parsedColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${player.jerseyNumber}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active Player",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "${player.playingRole} • ${player.favouriteTeam.ifBlank { "Team Player" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Edit & Delete actions
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Player", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Player", tint = CricketBallRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player technical traits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TraitBadge(label = "Bat", value = player.battingHand.replace("-handed", ""))
                TraitBadge(label = "Arm", value = player.bowlingArm.replace("-arm", ""))
                TraitBadge(label = "Style", value = player.bowlingStyle)
            }

            if (player.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = player.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Select as Active button
            if (!isSelected) {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Select as Active Career")
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Currently Active Career",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun TraitBadge(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlayerDialog(
    player: Player?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        fullName: String,
        jerseyNumber: Int,
        dateOfBirth: String,
        battingHand: String,
        bowlingArm: String,
        bowlingStyle: String,
        playingRole: String,
        favouriteTeam: String,
        description: String,
        avatarColorHex: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf(player?.fullName ?: "") }
    var jerseyNumberStr by remember { mutableStateOf(player?.jerseyNumber?.toString() ?: "18") }
    var dateOfBirth by remember { mutableStateOf(player?.dateOfBirth ?: "") }
    var battingHand by remember { mutableStateOf(player?.battingHand ?: "Right-handed") }
    var bowlingArm by remember { mutableStateOf(player?.bowlingArm ?: "Right-arm") }
    var bowlingStyle by remember { mutableStateOf(player?.bowlingStyle ?: "Medium pace") }
    var playingRole by remember { mutableStateOf(player?.playingRole ?: "All-rounder") }
    var favouriteTeam by remember { mutableStateOf(player?.favouriteTeam ?: "") }
    var description by remember { mutableStateOf(player?.description ?: "") }
    var avatarColorHex by remember { mutableStateOf(player?.avatarColorHex ?: "#059669") }

    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var battingDropdownExpanded by remember { mutableStateOf(false) }
    var bowlingArmDropdownExpanded by remember { mutableStateOf(false) }
    var bowlingStyleDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val colorOptions = listOf(
        "#059669", "#10B981", "#2563EB", "#7C3AED", "#DC2626", "#D97706", "#0D9488", "#475569"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (player != null) "Edit Player Profile" else "New Player Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = CricketBallRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("player_fullname_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jerseyNumberStr,
                        onValueChange = { jerseyNumberStr = it },
                        label = { Text("Jersey #") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("player_jersey_input")
                    )
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("DOB (Optional)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Playing Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = playingRole,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Playing Role") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        listOf("Batter", "Bowler", "All-rounder", "Wicketkeeper").forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role) },
                                onClick = {
                                    playingRole = role
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Batting Hand & Bowling Arm
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = battingDropdownExpanded,
                        onExpandedChange = { battingDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = battingHand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Batting Hand") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = battingDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = battingDropdownExpanded,
                            onDismissRequest = { battingDropdownExpanded = false }
                        ) {
                            listOf("Right-handed", "Left-handed").forEach { hand ->
                                DropdownMenuItem(
                                    text = { Text(hand) },
                                    onClick = {
                                        battingHand = hand
                                        battingDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = bowlingArmDropdownExpanded,
                        onExpandedChange = { bowlingArmDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = bowlingArm,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bowling Arm") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bowlingArmDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = bowlingArmDropdownExpanded,
                            onDismissRequest = { bowlingArmDropdownExpanded = false }
                        ) {
                            listOf("Right-arm", "Left-arm").forEach { arm ->
                                DropdownMenuItem(
                                    text = { Text(arm) },
                                    onClick = {
                                        bowlingArm = arm
                                        bowlingArmDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bowling Style Dropdown
                ExposedDropdownMenuBox(
                    expanded = bowlingStyleDropdownExpanded,
                    onExpandedChange = { bowlingStyleDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = bowlingStyle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bowling Style") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bowlingStyleDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = bowlingStyleDropdownExpanded,
                        onDismissRequest = { bowlingStyleDropdownExpanded = false }
                    ) {
                        listOf(
                            "Fast", "Fast-medium", "Medium pace", "Off-spin",
                            "Leg-spin", "Left-arm orthodox", "Left-arm wrist spin", "Other"
                        ).forEach { style ->
                            DropdownMenuItem(
                                text = { Text(style) },
                                onClick = {
                                    bowlingStyle = style
                                    bowlingStyleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = favouriteTeam,
                    onValueChange = { favouriteTeam = it },
                    label = { Text("Favourite Team (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Player Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Color Avatar Selection
                Text("Avatar Color Theme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colorOptions.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { avatarColorHex = hex }
                                .then(
                                    if (avatarColorHex == hex) {
                                        Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else Modifier
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Please enter the player's full name."
                                return@Button
                            }
                            onSave(
                                player?.id,
                                fullName.trim(),
                                jerseyNumberStr.toIntOrNull() ?: 18,
                                dateOfBirth.trim(),
                                battingHand,
                                bowlingArm,
                                bowlingStyle,
                                playingRole,
                                favouriteTeam.trim(),
                                description.trim(),
                                avatarColorHex
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("save_player_button")
                    ) {
                        Text(if (player != null) "Update Player" else "Save Player")
                    }
                }
            }
        }
    }
}
