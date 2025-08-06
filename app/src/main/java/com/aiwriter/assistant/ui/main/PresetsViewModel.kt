package com.aiwriter.assistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.WritingPreset
import com.aiwriter.assistant.data.repository.PresetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class PresetsViewModel : ViewModel() {
    
    private val app = AIWriterApplication.instance
    private val presetRepository = PresetRepository(app.database.writingPresetDao())
    
    val presets = presetRepository.getAllPresets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun createPreset(name: String, description: String, systemPrompt: String) {
        viewModelScope.launch {
            val preset = WritingPreset(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                systemPrompt = systemPrompt
            )
            presetRepository.insertPreset(preset)
        }
    }
    
    fun updatePreset(preset: WritingPreset) {
        viewModelScope.launch {
            presetRepository.updatePreset(preset)
        }
    }
    
    fun deletePreset(preset: WritingPreset) {
        viewModelScope.launch {
            presetRepository.deletePreset(preset)
        }
    }
    
    fun setDefaultPreset(presetId: String) {
        viewModelScope.launch {
            presetRepository.setDefaultPreset(presetId)
        }
    }
}