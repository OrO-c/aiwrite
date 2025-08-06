package com.aiwriter.assistant.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.AIWriterApplication
import com.aiwriter.assistant.ui.onboarding.OnboardingActivity
import com.aiwriter.assistant.ui.theme.AIWritingAssistantTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val preferences = AIWriterApplication.instance.preferences
        
        // Check if this is first launch
        if (preferences.isFirstLaunch) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        setContent {
            AIWritingAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (uiState.currentPage) {
        MainPage.DASHBOARD -> DashboardScreen(
            onNavigateToPresets = { viewModel.navigateToPage(MainPage.PRESETS) },
            onNavigateToSettings = { viewModel.navigateToPage(MainPage.SETTINGS) }
        )
        MainPage.PRESETS -> PresetsScreen(
            onNavigateBack = { viewModel.navigateToPage(MainPage.DASHBOARD) }
        )
        MainPage.SETTINGS -> SettingsScreen(
            onNavigateBack = { viewModel.navigateToPage(MainPage.DASHBOARD) }
        )
    }
}