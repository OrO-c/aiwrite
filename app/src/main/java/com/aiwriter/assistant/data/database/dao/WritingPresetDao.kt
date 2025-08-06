package com.aiwriter.assistant.data.database.dao

import androidx.room.*
import com.aiwriter.assistant.data.model.WritingPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface WritingPresetDao {
    
    @Query("SELECT * FROM writing_presets ORDER BY isDefault DESC, updatedAt DESC")
    fun getAllPresets(): Flow<List<WritingPreset>>
    
    @Query("SELECT * FROM writing_presets WHERE id = :id")
    suspend fun getPresetById(id: String): WritingPreset?
    
    @Query("SELECT * FROM writing_presets WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultPreset(): WritingPreset?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: WritingPreset)
    
    @Update
    suspend fun updatePreset(preset: WritingPreset)
    
    @Delete
    suspend fun deletePreset(preset: WritingPreset)
    
    @Query("DELETE FROM writing_presets WHERE id = :id")
    suspend fun deletePresetById(id: String)
    
    @Query("UPDATE writing_presets SET isDefault = 0")
    suspend fun clearDefaultFlags()
}