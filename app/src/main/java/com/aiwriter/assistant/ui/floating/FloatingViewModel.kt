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
        android.util.Log.d("FloatingViewModel", "loadPresets called")
        viewModelScope.launch {
            try {
                val presets = presetRepository.getAllPresets()
                android.util.Log.d("FloatingViewModel", "Got presets flow")
                
                presets.collect { presetList ->
                    android.util.Log.d("FloatingViewModel", "Preset list updated: ${presetList.size} presets")
                    presetList.forEach { preset ->
                        android.util.Log.d("FloatingViewModel", "Preset: ${preset.name}, isDefault: ${preset.isDefault}")
                    }
                    
                    if (presetList.isEmpty() && !initializedDefaults) {
                        android.util.Log.d("FloatingViewModel", "Initializing default presets")
                        // Try initialize defaults once
                        initializedDefaults = true
                        try {
                            presetRepository.initializeDefaultPresets()
                            android.util.Log.d("FloatingViewModel", "Default presets initialized")
                            // Don't return here, let the flow continue to update UI
                        } catch (e: Exception) { 
                            android.util.Log.e("FloatingViewModel", "Failed to initialize defaults", e)
                            _uiState.value = _uiState.value.copy(
                                error = "初始化默认预设失败: ${e.message}"
                            )
                        }
                    } else {
                        val defaultPreset = presetList.find { it.isDefault } ?: presetList.firstOrNull()
                        android.util.Log.d("FloatingViewModel", "Selected default preset: ${defaultPreset?.name}")
                        
                        _uiState.value = _uiState.value.copy(
                            availablePresets = presetList,
                            currentPreset = defaultPreset?.name ?: "",
                            error = null
                        )
                        currentPresetObject = defaultPreset
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("FloatingViewModel", "loadPresets failed", e)
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
        android.util.Log.d("FloatingViewModel", "selectPreset called with: $presetName")
        android.util.Log.d("FloatingViewModel", "Available presets: ${_uiState.value.availablePresets.map { it.name }}")
        
        val preset = _uiState.value.availablePresets.find { it.name == presetName }
        preset?.let {
            currentPresetObject = it
            _uiState.value = _uiState.value.copy(currentPreset = presetName)
            android.util.Log.d("FloatingViewModel", "Preset selected: ${it.name}")
        } ?: run {
            android.util.Log.w("FloatingViewModel", "Preset not found: $presetName")
        }
    }
    
    fun generateText() {
        val inputText = _uiState.value.inputText.trim()
        val preset = currentPresetObject
        
        android.util.Log.d("FloatingViewModel", "generateText called with input: $inputText, preset: ${preset?.name}")
        
        if (inputText.isBlank() || preset == null) {
            android.util.Log.w("FloatingViewModel", "Invalid input: inputText=$inputText, preset=$preset")
            _uiState.value = _uiState.value.copy(error = "请输入主题并选择预设")
            return
        }
        
        val apiConfig = preferences.getCurrentApiConfig()
        android.util.Log.d("FloatingViewModel", "API config: $apiConfig")
        
        if (apiConfig == null || !preferences.hasValidApiConfig()) {
            android.util.Log.w("FloatingViewModel", "Invalid API config: $apiConfig")
            _uiState.value = _uiState.value.copy(error = "请先配置AI模型")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        android.util.Log.d("FloatingViewModel", "Starting generation...")
        
        viewModelScope.launch {
            try {
                val result = aiRepository.generateThreeVersions(
                    input = inputText,
                    systemPrompt = preset.systemPrompt,
                    apiConfig = apiConfig
                )
                
                android.util.Log.d("FloatingViewModel", "Generation result: $result")
                
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
                    android.util.Log.d("FloatingViewModel", "Generation completed successfully")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "未知错误"
                    android.util.Log.e("FloatingViewModel", "Generation failed: $error")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "生成失败: $error"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("FloatingViewModel", "Generation exception", e)
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

    fun testApiConnection() {
        viewModelScope.launch {
            try {
                val apiConfig = preferences.getCurrentApiConfig()
                if (apiConfig == null || !preferences.hasValidApiConfig()) {
                    _uiState.value = _uiState.value.copy(error = "请先配置AI模型")
                    return@launch
                }
                
                android.util.Log.d("FloatingViewModel", "Testing API connection...")
                val result = aiRepository.testApiConnection(apiConfig)
                
                if (result.isSuccess) {
                    android.util.Log.d("FloatingViewModel", "API test successful")
                    _uiState.value = _uiState.value.copy(error = null)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "未知错误"
                    android.util.Log.e("FloatingViewModel", "API test failed: $error")
                    _uiState.value = _uiState.value.copy(error = "API连接测试失败: $error")
                }
            } catch (e: Exception) {
                android.util.Log.e("FloatingViewModel", "API test exception", e)
                _uiState.value = _uiState.value.copy(error = "API测试异常: ${e.message}")
            }
        }
    }
}