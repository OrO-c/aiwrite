package com.aiwriter.assistant.ui.floating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.GeneratedText
import com.aiwriter.assistant.data.model.WritingPreset
import com.aiwriter.assistant.data.repository.AITextRepository
import com.aiwriter.assistant.data.repository.PresetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class FloatingUiState(
    val isLoading: Boolean = false,
    val inputText: String = "",
    val currentPreset: String = "",
    val availablePresets: List<WritingPreset> = emptyList(),
    val generatedText: GeneratedText? = null,
    val error: String? = null
)

class FloatingViewModel : ViewModel() {
    
    private val app = AIWriterApplication.instance
    private val preferences = app.preferences
    private val database = app.database
    
    private val presetRepository = PresetRepository(database.writingPresetDao())
    private val aiRepository = AITextRepository()
    
    private val _uiState = MutableStateFlow(FloatingUiState())
    val uiState: StateFlow<FloatingUiState> = _uiState.asStateFlow()
    
    private var currentPresetObject: WritingPreset? = null
    private var initializedDefaults = false
    
    init {
        loadPresets()
    }
    
    private fun loadPresets() {
        viewModelScope.launch {
            try {
                val presets = presetRepository.getAllPresets()
                presets.collect { presetList ->
                    if (presetList.isEmpty() && !initializedDefaults) {
                        // Try initialize defaults once
                        initializedDefaults = true
                        try {
                            presetRepository.initializeDefaultPresets()
                            return@collect
                        } catch (_: Exception) { /* ignore */ }
                    }
                    val defaultPreset = presetList.find { it.isDefault } ?: presetList.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        availablePresets = presetList,
                        currentPreset = defaultPreset?.name ?: ""
                    )
                    currentPresetObject = defaultPreset
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "加载预设失败: ${e.message}"
                )
            }
        }
    }
    
    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text, error = null)
    }
    
    fun selectPreset(presetName: String) {
        val preset = _uiState.value.availablePresets.find { it.name == presetName }
        preset?.let {
            currentPresetObject = it
            _uiState.value = _uiState.value.copy(currentPreset = presetName)
        }
    }
    
    fun generateText() {
        val inputText = _uiState.value.inputText.trim()
        val preset = currentPresetObject
        
        if (inputText.isBlank() || preset == null) {
            _uiState.value = _uiState.value.copy(error = "请输入主题并选择预设")
            return
        }
        
        val apiConfig = preferences.getCurrentApiConfig()
        if (apiConfig == null || !preferences.hasValidApiConfig()) {
            _uiState.value = _uiState.value.copy(error = "请先配置AI模型")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val result = aiRepository.generateThreeVersions(
                    input = inputText,
                    systemPrompt = preset.systemPrompt,
                    apiConfig = apiConfig
                )
                
                if (result.isSuccess) {
                    val (version1, version2, version3) = result.getOrThrow()
                    
                    val generatedText = GeneratedText(
                        id = UUID.randomUUID().toString(),
                        input = inputText,
                        presetId = preset.id,
                        presetName = preset.name,
                        version1 = version1,
                        version2 = version2,
                        version3 = version3,
                        modelProvider = apiConfig.provider.displayName
                    )
                    
                    // Save to database
                    database.generatedTextDao().insertText(generatedText)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        generatedText = generatedText
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "生成失败: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "生成失败: ${e.message}"
                )
            }
        }
    }
    
    fun regenerateText() {
        val currentGenerated = _uiState.value.generatedText ?: return
        
        // Reset to input state with same input and preset
        _uiState.value = _uiState.value.copy(
            generatedText = null,
            inputText = currentGenerated.input
        )
        
        // Find and set the preset
        val preset = _uiState.value.availablePresets.find { it.id == currentGenerated.presetId }
        preset?.let {
            currentPresetObject = it
            _uiState.value = _uiState.value.copy(currentPreset = it.name)
        }
        
        // Regenerate
        generateText()
    }
    
    fun resetToInput() {
        _uiState.value = _uiState.value.copy(
            generatedText = null,
            inputText = "",
            error = null
        )
    }
}