package com.aiwriter.assistant.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.aiwriter.assistant.data.model.ApiConfig
import com.aiwriter.assistant.data.model.ApiProvider
import com.aiwriter.assistant.data.model.WorkMode
import com.google.gson.Gson

class AppPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "ai_writer_prefs"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_WORK_MODE = "work_mode"
        private const val KEY_API_CONFIGS = "api_configs"
        private const val KEY_CURRENT_API_PROVIDER = "current_api_provider"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_SETUP_COMPLETED = "setup_completed"
    }
    
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
    
    var workMode: WorkMode
        get() = try {
            WorkMode.valueOf(prefs.getString(KEY_WORK_MODE, WorkMode.TILE_CLIPBOARD.name)!!)
        } catch (e: Exception) {
            WorkMode.TILE_CLIPBOARD
        }
        set(value) = prefs.edit().putString(KEY_WORK_MODE, value.name).apply()
    
    var apiConfigs: Map<ApiProvider, ApiConfig>
        get() = try {
            val json = prefs.getString(KEY_API_CONFIGS, "{}")
            val configMap = mutableMapOf<ApiProvider, ApiConfig>()
            if (!json.isNullOrEmpty() && json != "{}") {
                val type = object : com.google.gson.reflect.TypeToken<Map<String, ApiConfig>>() {}.type
                val configs: Map<String, ApiConfig> = gson.fromJson(json, type)
                configs.forEach { (key, value) ->
                    try {
                        val provider = ApiProvider.valueOf(key)
                        configMap[provider] = value
                    } catch (e: Exception) {
                        // Ignore invalid provider
                    }
                }
            }
            configMap
        } catch (e: Exception) {
            emptyMap()
        }
        set(value) {
            val configMap = value.mapKeys { it.key.name }
            val json = gson.toJson(configMap)
            prefs.edit().putString(KEY_API_CONFIGS, json).apply()
        }
    
    var currentApiProvider: ApiProvider
        get() = try {
            ApiProvider.valueOf(prefs.getString(KEY_CURRENT_API_PROVIDER, ApiProvider.OPENAI.name)!!)
        } catch (e: Exception) {
            ApiProvider.OPENAI
        }
        set(value) = prefs.edit().putString(KEY_CURRENT_API_PROVIDER, value.name).apply()
    
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
    
    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()
    
    var isSetupCompleted: Boolean
        get() = prefs.getBoolean(KEY_SETUP_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_COMPLETED, value).apply()
    
    fun getCurrentApiConfig(): ApiConfig? {
        return apiConfigs[currentApiProvider]
    }
    
    fun hasValidApiConfig(): Boolean {
        val config = getCurrentApiConfig()
        return config != null && config.apiKey.isNotBlank()
    }
    
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}