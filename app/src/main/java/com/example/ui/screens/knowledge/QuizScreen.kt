package com.example.ui.screens.knowledge

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizResult
import com.example.engine.CricketQuizBank
import com.example.ui.theme.CricketBallRed
import com.example.ui.theme.CricketGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTurf
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizScreen(
    quizHistory: List<QuizResult>,
    onSaveQuizResult: (total: Int, correct: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf("All") }
    val difficultyFilters = listOf("All", "Easy", "Medium", "Hard")

    val activeQuestions = remember(selectedDifficulty) {
        if (selectedDifficulty == "All") CricketQuizBank.questions
        else CricketQuizBank.questions.filter { it.difficulty.equals(selectedDifficulty, ignoreCase = true) }
    }

    var currentIndex by remember(activeQuestions) { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var sessionScore by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }

    val currentQuestion = activeQuestions.getOrNull(currentIndex)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Daily Question Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldDark.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CricketGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CRICKET IQ CHALLENGE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = CricketGold
                        )
                        Text(
                            text = "Test your mastery of MCC laws, scoring formulas & playing conditions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Difficulty Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                items(difficultyFilters) { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = {
                            selectedDifficulty = diff
                            currentIndex = 0
                            selectedOptionIndex = null
                            hasAnswered = false
                            sessionScore = 0
                            quizFinished = false
                        },
                        label = { Text(diff) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (quizFinished) {
            // Results Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Quiz Completed!",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val pct = if (activeQuestions.isNotEmpty()) {
                            ((sessionScore.toDouble() / activeQuestions.size.toDouble()) * 100).toInt()
                        } else 0

                        Text(
                            text = "$pct%",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = if (pct >= 70) EmeraldPrimary else CricketGold
                            )
                        )

                        Text(
                            text = "You scored $sessionScore out of ${activeQuestions.size} correct",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                currentIndex = 0
                                selectedOptionIndex = null
                                hasAnswered = false
                                sessionScore = 0
                                quizFinished = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Quiz")
                        }
                    }
                }
            }
        } else if (currentQuestion != null) {
            // Current Question Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${activeQuestions.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = currentQuestion.difficulty.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (currentQuestion.difficulty.lowercase()) {
                                        "easy" -> EmeraldPrimary
                                        "medium" -> CricketGold
                                        else -> CricketBallRed
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options
                        currentQuestion.options.forEachIndexed { idx, optionText ->
                            val isSelected = selectedOptionIndex == idx
                            val isCorrect = idx == currentQuestion.correctIndex

                            val backgroundColor = when {
                                !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                isCorrect -> EmeraldPrimary.copy(alpha = 0.25f)
                                isSelected -> CricketBallRed.copy(alpha = 0.25f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val borderColor = when {
                                !hasAnswered -> if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline
                                isCorrect -> EmeraldPrimary
                                isSelected -> CricketBallRed
                                else -> MaterialTheme.colorScheme.outline
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = backgroundColor,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !hasAnswered) {
                                        selectedOptionIndex = idx
                                        hasAnswered = true
                                        if (isCorrect) sessionScore++
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${('A'.code + idx).toChar()}.",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (hasAnswered && isCorrect) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (hasAnswered) {
                                        if (isCorrect) {
                                            Icon(Icons.Default.Check, contentDescription = "Correct", tint = EmeraldPrimary)
                                        } else if (isSelected) {
                                            Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = CricketBallRed)
                                        }
                                    }
                                }
                            }
                        }

                        // Instant Explanation Feedback
                        if (hasAnswered) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = CardDefaults.outlinedCardBorder(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (selectedOptionIndex == currentQuestion.correctIndex) "✓ Correct!" else "✗ Incorrect",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selectedOptionIndex == currentQuestion.correctIndex) EmeraldPrimary else CricketBallRed
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (currentIndex + 1 < activeQuestions.size) {
                                        currentIndex++
                                        selectedOptionIndex = null
                                        hasAnswered = false
                                    } else {
                                        quizFinished = true
                                        onSaveQuizResult(activeQuestions.size, sessionScore)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (currentIndex + 1 < activeQuestions.size) "Next Question →" else "Finish Quiz & Save Result"
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quiz History
        if (quizHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "PAST QUIZ ATTEMPTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(quizHistory.take(5)) { attempt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Score: ${attempt.correctAnswers} / ${attempt.totalQuestions}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(attempt.dateAttempted))
                            Text(text = dateStr, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }

                        Text(
                            text = "${attempt.scorePercentage}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (attempt.scorePercentage >= 70) EmeraldPrimary else CricketGold
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
