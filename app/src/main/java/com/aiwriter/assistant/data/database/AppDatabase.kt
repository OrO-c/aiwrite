package com.aiwriter.assistant.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aiwriter.assistant.data.database.dao.GeneratedTextDao
import com.aiwriter.assistant.data.database.dao.WritingPresetDao
import com.aiwriter.assistant.data.model.GeneratedText
import com.aiwriter.assistant.data.model.WritingPreset

@Database(
    entities = [WritingPreset::class, GeneratedText::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun writingPresetDao(): WritingPresetDao
    abstract fun generatedTextDao(): GeneratedTextDao
}