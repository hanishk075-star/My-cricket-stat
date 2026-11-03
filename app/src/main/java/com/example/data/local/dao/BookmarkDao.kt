package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.QuizHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM knowledge_bookmarks")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM knowledge_bookmarks WHERE topicId = :topicId")
    suspend fun deleteBookmark(topicId: String)

    @Query("SELECT * FROM quiz_history ORDER BY attemptedAt DESC")
    fun getQuizHistory(): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(record: QuizHistoryEntity)

    @Query("DELETE FROM quiz_history")
    suspend fun clearQuizHistory()
}
