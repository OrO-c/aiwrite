package com.aiwriter.assistant.ui.onboarding

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.data.model.ApiConfig
import com.aiwriter.assistant.data.model.ApiProvider
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.data.repository.AITextRepository
import com.aiwriter.assistant.data.repository.PresetRepository
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    
    private val app = AIWriterApplication.instance
    private val preferences = app.preferences
    private val presetRepository = PresetRepository(app.database.writingPresetDao())
    private val aiRepository = AITextRepository()
    
    var selectedWorkMode = mutableStateOf(WorkMode.TILE_CLIPBOARD)
        private set
    
    var selectedApiProvider = mutableStateOf(ApiProvider.OPENAI)
        private set
    
    var apiKey = mutableStateOf("")
        private set
    
    var customEndpoint = mutableStateOf("")
        private set
    
    var isTestingConnection = mutableStateOf(false)
        private set
    
    var connectionTestResult = mutableStateOf<String?>(null)
        private set
    
    fun selectWorkMode(mode: WorkMode) {
        selectedWorkMode.value = mode
    }
    
    fun selectApiProvider(provider: ApiProvider) {
        selectedApiProvider.value = provider
        if (provider == ApiProvider.CUSTOM) {
            customEndpoint.value = ""
        } else {
            customEndpoint.value = provider.defaultEndpoint
        }
    }
    
    fun updateApiKey(key: String) {
        apiKey.value = key
        connectionTestResult.value = null
    }
    
    fun updateCustomEndpoint(endpoint: String) {
        customEndpoint.value = endpoint
        connectionTestResult.value = null
    }
    
    fun testApiConnection() {
        if (apiKey.value.isBlank()) {
            connectionTestResult.value = "请输入API密钥"
            return
        }
        
        val endpoint = if (selectedApiProvider.value == ApiProvider.CUSTOM) {
            customEndpoint.value
        } else {
            selectedApiProvider.value.defaultEndpoint
        }
        
        if (endpoint.isBlank()) {
            connectionTestResult.value = "请输入API端点"
            return
        }
        
        isTestingConnection.value = true
        connectionTestResult.value = null
        
        viewModelScope.launch {
            try {
                val config = ApiConfig(
                    provider = selectedApiProvider.value,
                    apiKey = apiKey.value,
                    endpoint = endpoint
                )
                
                val result = aiRepository.testApiConnection(config)
                
                if (result.isSuccess) {
                    connectionTestResult.value = "✅ 连接成功"
                    saveApiConfig(config)
                } else {
                    connectionTestResult.value = "❌ 连接失败: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                connectionTestResult.value = "❌ 连接失败: ${e.message}"
            } finally {
                isTestingConnection.value = false
            }
        }
    }
    
    private fun saveApiConfig(config: ApiConfig) {
        val currentConfigs = preferences.apiConfigs.toMutableMap()
        currentConfigs[config.provider] = config
        preferences.apiConfigs = currentConfigs
        preferences.currentApiProvider = config.provider
    }
    
    fun isConfigurationValid(): Boolean {
        return apiKey.value.isNotBlank() && 
               connectionTestResult.value?.startsWith("✅") == true
    }
    
    fun completeOnboarding() {
        preferences.workMode = selectedWorkMode.value
        preferences.isFirstLaunch = false
        preferences.isSetupCompleted = true
        
        // Initialize default presets
        viewModelScope.launch {
            presetRepository.initializeDefaultPresets()
        }
    }
}