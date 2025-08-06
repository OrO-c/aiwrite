package com.aiwriter.assistant.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.GeneratedText
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.data.repository.PresetRepository
import com.aiwriter.assistant.utils.PermissionHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    
    private val app = AIWriterApplication.instance
    private val preferences = app.preferences
    private val database = app.database
    
    private val presetRepository = PresetRepository(database.writingPresetDao())
    
    val recentTexts = database.generatedTextDao().getRecentTexts(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val workMode = flow {
        emit(preferences.workMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkMode.TILE_CLIPBOARD
    )
    
    private val _missingPermissions = MutableStateFlow<List<String>>(emptyList())
    val missingPermissions = _missingPermissions.asStateFlow()
    
    init {
        updatePermissionStatus()
    }
    
    private fun updatePermissionStatus() {
        val context = app.applicationContext
        val requireFloating = preferences.workMode == WorkMode.FLOATING_INPUT
        _missingPermissions.value = PermissionHelper.getMissingPermissions(context, requireFloating)
    }
    
    fun requestPermissions() {
        // This would typically launch permission request activities
        // For now, we'll just update the status
        updatePermissionStatus()
    }
    
    fun startNewGeneration() {
        // This would launch the floating window or trigger tile action
        // Implementation depends on the selected work mode
    }
    
    fun regenerateText(text: GeneratedText) {
        viewModelScope.launch {
            // TODO: Implement regeneration logic
        }
    }
    
    fun deleteText(text: GeneratedText) {
        viewModelScope.launch {
            database.generatedTextDao().deleteText(text)
        }
    }
}