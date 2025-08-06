package com.aiwriter.assistant.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aiwriter.assistant.data.model.GeneratedText
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedTextDao {
    
    @Query("SELECT * FROM generated_texts ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentTexts(limit: Int = 10): Flow<List<GeneratedText>>
    
    @Query("SELECT * FROM generated_texts WHERE id = :id")
    suspend fun getTextById(id: String): GeneratedText?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(text: GeneratedText)
    
    @Delete
    suspend fun deleteText(text: GeneratedText)
    
    @Query("DELETE FROM generated_texts WHERE id = :id")
    suspend fun deleteTextById(id: String)
    
    @Query("DELETE FROM generated_texts")
    suspend fun deleteAllTexts()
    
    @Query("SELECT COUNT(*) FROM generated_texts")
    suspend fun getTextCount(): Int
}