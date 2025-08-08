package com.aiwriter.assistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.ApiConfig
import com.aiwriter.assistant.data.model.ApiProvider
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.data.repository.AITextRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val workMode: WorkMode = WorkMode.TILE_CLIPBOARD,
    val currentApiProvider: ApiProvider = ApiProvider.OPENAI,
    val apiConfigs: Map<ApiProvider, ApiConfig> = emptyMap(),
    val isDarkMode: Boolean = false,
    val isVibrationEnabled: Boolean = true
)

class SettingsViewModel : ViewModel() {
    
    private val app = AIWriterApplication.instance
    private val preferences = app.preferences
    private val database = app.database
    private val aiRepository = AITextRepository()
    
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            workMode = preferences.workMode,
            currentApiProvider = preferences.currentApiProvider,
            apiConfigs = preferences.apiConfigs,
            isDarkMode = preferences.isDarkMode,
            isVibrationEnabled = preferences.isVibrationEnabled
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    fun updateWorkMode(mode: WorkMode) {
        preferences.workMode = mode
        _uiState.value = _uiState.value.copy(workMode = mode)
    }
    
    fun updateCurrentProvider(provider: ApiProvider) {
        preferences.currentApiProvider = provider
        _uiState.value = _uiState.value.copy(currentApiProvider = provider)
    }
    
    fun updateApiConfig(config: ApiConfig) {
        val currentConfigs = preferences.apiConfigs.toMutableMap()
        currentConfigs[config.provider] = config
        preferences.apiConfigs = currentConfigs
        _uiState.value = _uiState.value.copy(apiConfigs = currentConfigs)
    }
    
    fun updateDarkMode(enabled: Boolean) {
        preferences.isDarkMode = enabled
        _uiState.value = _uiState.value.copy(isDarkMode = enabled)
    }
    
    fun updateVibration(enabled: Boolean) {
        preferences.isVibrationEnabled = enabled
        _uiState.value = _uiState.value.copy(isVibrationEnabled = enabled)
    }
    
    fun testConnection(config: ApiConfig) {
        viewModelScope.launch {
            try {
                val result = aiRepository.testApiConnection(config)
                // Handle test result
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            database.generatedTextDao().deleteAllTexts()
        }
    }
    
    fun resetApp() {
        viewModelScope.launch {
            // Clear database
            database.generatedTextDao().deleteAllTexts()
            database.writingPresetDao().deleteAllPresets()
            
            // Clear preferences
            preferences.clearAllData()
        }
    }
}