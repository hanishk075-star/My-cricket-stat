package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MatchRecord
import com.example.ui.screens.analytics.AnalyticsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.knowledge.KnowledgeScreen
import com.example.ui.screens.matches.AddEditMatchDialog
import com.example.ui.screens.matches.MatchDetailDialog
import com.example.ui.screens.matches.MatchesScreen
import com.example.ui.screens.players.PlayersScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.MyCricketStatsTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.CricketViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CricketViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            var isSettingsOpen by remember { mutableStateOf(false) }
            var showGlobalAddMatchDialog by remember { mutableStateOf(false) }
            var matchDetailToShow by remember { mutableStateOf<MatchRecord?>(null) }
            var matchToEditFromHome by remember { mutableStateOf<MatchRecord?>(null) }

            val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
            val allPlayers by viewModel.allPlayers.collectAsStateWithLifecycle()
            val selectedPlayer by viewModel.selectedPlayer.collectAsStateWithLifecycle()
            val selectedPlayerMatches by viewModel.selectedPlayerMatches.collectAsStateWithLifecycle()
            val selectedPlayerStats by viewModel.selectedPlayerStats.collectAsStateWithLifecycle()

            val knowledgeItems by viewModel.filteredKnowledgeItems.collectAsStateWithLifecycle()
            val knowledgeCategory by viewModel.knowledgeCategory.collectAsStateWithLifecycle()
            val knowledgeSearchQuery by viewModel.knowledgeSearchQuery.collectAsStateWithLifecycle()
            val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()

            MyCricketStatsTheme(darkTheme = isDarkTheme) {
                // Back handler when settings or non-home tabs are active
                BackHandler(enabled = isSettingsOpen || activeTab != AppTab.HOME) {
                    if (isSettingsOpen) {
                        isSettingsOpen = false
                    } else if (activeTab != AppTab.HOME) {
                        viewModel.setTab(AppTab.HOME)
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Custom app logo
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EmeraldDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.cricket_stats_icon_1793707510299),
                                            contentDescription = "MyCricketStats Logo",
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = if (isSettingsOpen) "Settings" else "MyCricketStats",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            actions = {
                                if (!isSettingsOpen) {
                                    // Active player quick chip in top bar
                                    if (selectedPlayer != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(EmeraldPrimary.copy(alpha = 0.16f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#${selectedPlayer?.jerseyNumber} ${selectedPlayer?.fullName?.split(" ")?.firstOrNull() ?: ""}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    IconButton(
                                        onClick = { isSettingsOpen = true },
                                        modifier = Modifier.testTag("top_settings_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Open Settings",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = { isSettingsOpen = false },
                                        modifier = Modifier.testTag("close_settings_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Back to Dashboard",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    bottomBar = {
                        if (!isSettingsOpen) {
                            NavigationBar(
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .testTag("bottom_nav_bar"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val navItems = listOf(
                                    Triple(AppTab.HOME, "Home", Icons.Default.Home),
                                    Triple(AppTab.PLAYERS, "Players", Icons.Default.Group),
                                    Triple(AppTab.MATCHES, "Matches", Icons.Default.SportsCricket),
                                    Triple(AppTab.ANALYTICS, "Analytics", Icons.Default.BarChart),
                                    Triple(AppTab.KNOWLEDGE, "Knowledge", Icons.Default.MenuBook)
                                )

                                navItems.forEach { (tab, label, icon) ->
                                    val isSelected = activeTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setTab(tab) },
                                        icon = { Icon(imageVector = icon, contentDescription = label) },
                                        label = { Text(text = label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = EmeraldDark,
                                            selectedTextColor = EmeraldPrimary,
                                            indicatorColor = EmeraldPrimary,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (isSettingsOpen) {
                            SettingsScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = it },
                                selectedPlayer = selectedPlayer,
                                matches = selectedPlayerMatches,
                                careerStats = selectedPlayerStats,
                                onExportBackup = { viewModel.exportBackupJson() },
                                onImportBackup = { json, merge -> viewModel.importBackup(json, merge) },
                                onClearAllData = { viewModel.clearAllData() }
                            )
                        } else {
                            when (activeTab) {
                                AppTab.HOME -> HomeScreen(
                                    players = allPlayers,
                                    selectedPlayer = selectedPlayer,
                                    matches = selectedPlayerMatches,
                                    careerStats = selectedPlayerStats,
                                    onSelectPlayer = { viewModel.selectPlayer(it) },
                                    onNavigateToPlayers = { viewModel.setTab(AppTab.PLAYERS) },
                                    onNavigateToMatches = { viewModel.setTab(AppTab.MATCHES) },
                                    onAddMatchClick = { showGlobalAddMatchDialog = true },
                                    onMatchClick = { matchDetailToShow = it }
                                )

                                AppTab.PLAYERS -> PlayersScreen(
                                    players = allPlayers,
                                    selectedPlayer = selectedPlayer,
                                    onSelectPlayer = {
                                        viewModel.selectPlayer(it)
                                        viewModel.setTab(AppTab.HOME)
                                    },
                                    onSavePlayer = { id, fullName, jerseyNumber, dob, battingHand, bowlingArm, bowlingStyle, role, favTeam, desc, colorHex ->
                                        viewModel.savePlayer(id, fullName, jerseyNumber, dob, battingHand, bowlingArm, bowlingStyle, role, favTeam, desc, colorHex)
                                    },
                                    onDeletePlayer = { viewModel.deletePlayer(it) }
                                )

                                AppTab.MATCHES -> MatchesScreen(
                                    players = allPlayers,
                                    selectedPlayer = selectedPlayer,
                                    matches = selectedPlayerMatches,
                                    onSelectPlayer = { viewModel.selectPlayer(it) },
                                    onNavigateToPlayers = { viewModel.setTab(AppTab.PLAYERS) },
                                    onSaveMatch = { match, isEdit -> viewModel.saveMatch(match, isEdit) },
                                    onDeleteMatch = { viewModel.deleteMatch(it) }
                                )

                                AppTab.ANALYTICS -> AnalyticsScreen(
                                    players = allPlayers,
                                    selectedPlayer = selectedPlayer,
                                    matches = selectedPlayerMatches,
                                    careerStats = selectedPlayerStats,
                                    onSelectPlayer = { viewModel.selectPlayer(it) },
                                    onNavigateToPlayers = { viewModel.setTab(AppTab.PLAYERS) }
                                )

                                AppTab.KNOWLEDGE -> KnowledgeScreen(
                                    items = knowledgeItems,
                                    selectedCategory = knowledgeCategory,
                                    searchQuery = knowledgeSearchQuery,
                                    quizHistory = quizHistory,
                                    onSelectCategory = { viewModel.setKnowledgeCategory(it) },
                                    onSearchQueryChange = { viewModel.setKnowledgeSearchQuery(it) },
                                    onToggleBookmark = { viewModel.toggleBookmark(it) },
                                    onSaveQuizResult = { total, correct -> viewModel.recordQuizCompletion(total, correct) }
                                )
                            }
                        }
                    }

                    // Global Add Match Dialog triggered from Home screen
                    if (showGlobalAddMatchDialog) {
                        AddEditMatchDialog(
                            matchToEdit = null,
                            players = allPlayers,
                            defaultPlayerId = selectedPlayer?.id,
                            onDismiss = { showGlobalAddMatchDialog = false },
                            onSave = { match, isEdit ->
                                viewModel.saveMatch(match, isEdit)
                                showGlobalAddMatchDialog = false
                            }
                        )
                    }

                    // Match Detail Dialog from Home recent list
                    if (matchDetailToShow != null) {
                        MatchDetailDialog(
                            match = matchDetailToShow!!,
                            onDismiss = { matchDetailToShow = null },
                            onEdit = {
                                matchToEditFromHome = matchDetailToShow
                                matchDetailToShow = null
                            }
                        )
                    }

                    if (matchToEditFromHome != null) {
                        AddEditMatchDialog(
                            matchToEdit = matchToEditFromHome,
                            players = allPlayers,
                            defaultPlayerId = selectedPlayer?.id,
                            onDismiss = { matchToEditFromHome = null },
                            onSave = { match, isEdit ->
                                viewModel.saveMatch(match, isEdit)
                                matchToEditFromHome = null
                            }
                        )
                    }
                }
            }
        }
    }
}
