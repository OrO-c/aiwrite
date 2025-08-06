package com.aiwriter.assistant

import android.app.Application
import androidx.room.Room
import com.aiwriter.assistant.data.database.AppDatabase
import com.aiwriter.assistant.data.preferences.AppPreferences

class AIWriterApplication : Application() {
    
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "ai_writer_database"
        ).build()
    }
    
    val preferences by lazy {
        AppPreferences(applicationContext)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: AIWriterApplication
            private set
    }
}