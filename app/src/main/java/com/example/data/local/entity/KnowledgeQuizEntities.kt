package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_bookmarks")
data class BookmarkEntity(
    @PrimaryKey val topicId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercentage: Int,
    val attemptedAt: Long = System.currentTimeMillis()
)
