package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CricketDatabase
import com.example.data.model.CareerStatistics
import com.example.data.model.KnowledgeItem
import com.example.data.model.MatchRecord
import com.example.data.model.Player
import com.example.data.model.QuizResult
import com.example.data.repository.CricketRepository
import com.example.engine.CricketKnowledgeBank
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val label: String) {
    HOME("Home"),
    PLAYERS("Players"),
    MATCHES("Matches"),
    ANALYTICS("Analytics"),
    KNOWLEDGE("Knowledge")
}

@OptIn(ExperimentalCoroutinesApi::class)
class CricketViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CricketDatabase.getInstance(application)
    private val repository = CricketRepository(db)

    private val _activeTab = MutableStateFlow(AppTab.HOME)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    private val _selectedPlayerId = MutableStateFlow<String?>(null)
    val selectedPlayerId: StateFlow<String?> = _selectedPlayerId.asStateFlow()

    val allPlayers: StateFlow<List<Player>> = repository.allPlayers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedPlayer: StateFlow<Player?> = combine(allPlayers, _selectedPlayerId) { players, selectedId ->
        if (players.isEmpty()) null
        else players.firstOrNull { it.id == selectedId } ?: players.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val selectedPlayerMatches: StateFlow<List<MatchRecord>> = selectedPlayer
        .flatMapLatest { player ->
            if (player == null) flowOf(emptyList())
            else repository.getMatchesForPlayer(player.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedPlayerStats: StateFlow<CareerStatistics?> = combine(selectedPlayer, selectedPlayerMatches) { player, matches ->
        if (player == null) null
        else repository.calculateCareerStats(matches, player.id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val bookmarkedTopicIds: StateFlow<Set<String>> = repository.bookmarkedTopicIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val quizHistory: StateFlow<List<QuizResult>> = repository.quizHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Knowledge state
    private val _knowledgeCategory = MutableStateFlow("All")
    val knowledgeCategory: StateFlow<String> = _knowledgeCategory.asStateFlow()

    private val _knowledgeSearchQuery = MutableStateFlow("")
    val knowledgeSearchQuery: StateFlow<String> = _knowledgeSearchQuery.asStateFlow()

    val filteredKnowledgeItems: StateFlow<List<KnowledgeItem>> = combine(
        _knowledgeCategory,
        _knowledgeSearchQuery,
        bookmarkedTopicIds
    ) { category, query, bookmarks ->
        CricketKnowledgeBank.items.filter { item ->
            val matchesCategory = when (category) {
                "All" -> true
                "Bookmarks" -> bookmarks.contains(item.id)
                else -> item.category.equals(category, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.title.lowercase().contains(q) ||
                        item.summary.lowercase().contains(q) ||
                        item.fullExplanation.lowercase().contains(q) ||
                        item.tags.any { it.lowercase().contains(q) }
            }
            matchesCategory && matchesQuery
        }.map { item ->
            item.copy(isBookmarked = bookmarks.contains(item.id))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CricketKnowledgeBank.items
    )

    init {
        // Auto-select first player once loaded if none selected
        viewModelScope.launch {
            allPlayers.collect { players ->
                if (_selectedPlayerId.value == null && players.isNotEmpty()) {
                    _selectedPlayerId.value = players.first().id
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun selectPlayer(player: Player) {
        _selectedPlayerId.value = player.id
    }

    fun selectPlayerById(id: String) {
        _selectedPlayerId.value = id
    }

    fun savePlayer(
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
    ) {
        viewModelScope.launch {
            val playerId = id ?: "player_${UUID.randomUUID()}"
            val player = Player(
                id = playerId,
                fullName = fullName.trim(),
                jerseyNumber = jerseyNumber,
                dateOfBirth = dateOfBirth.trim(),
                battingHand = battingHand,
                bowlingArm = bowlingArm,
                bowlingStyle = bowlingStyle,
                playingRole = playingRole,
                favouriteTeam = favouriteTeam.trim(),
                description = description.trim(),
                avatarColorHex = avatarColorHex,
                createdAt = System.currentTimeMillis()
            )
            if (id == null) {
                repository.addPlayer(player)
                _selectedPlayerId.value = playerId
            } else {
                repository.updatePlayer(player)
            }
        }
    }

    fun deletePlayer(playerId: String) {
        viewModelScope.launch {
            repository.deletePlayer(playerId)
            if (_selectedPlayerId.value == playerId) {
                _selectedPlayerId.value = null
            }
        }
    }

    fun saveMatch(match: MatchRecord, isEdit: Boolean) {
        viewModelScope.launch {
            if (isEdit) {
                repository.updateMatch(match)
            } else {
                repository.addMatch(match)
            }
        }
    }

    fun deleteMatch(matchId: String) {
        viewModelScope.launch {
            repository.deleteMatch(matchId)
        }
    }

    fun setKnowledgeCategory(cat: String) {
        _knowledgeCategory.value = cat
    }

    fun setKnowledgeSearchQuery(query: String) {
        _knowledgeSearchQuery.value = query
    }

    fun toggleBookmark(topicId: String) {
        viewModelScope.launch {
            val currently = bookmarkedTopicIds.value.contains(topicId)
            repository.toggleBookmark(topicId, currently)
        }
    }

    fun recordQuizCompletion(total: Int, correct: Int) {
        viewModelScope.launch {
            val percentage = if (total > 0) ((correct.toDouble() / total.toDouble()) * 100).toInt() else 0
            repository.recordQuizResult(
                QuizResult(
                    totalQuestions = total,
                    correctAnswers = correct,
                    scorePercentage = percentage
                )
            )
        }
    }

    suspend fun exportBackupJson(): String = repository.exportBackupJson()

    suspend fun importBackup(jsonString: String, merge: Boolean): Result<Int> {
        return repository.importBackupJson(jsonString, merge)
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _selectedPlayerId.value = null
        }
    }
}
