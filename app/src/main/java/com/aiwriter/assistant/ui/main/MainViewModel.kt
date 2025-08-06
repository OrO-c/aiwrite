package com.aiwriter.assistant.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MainPage {
    DASHBOARD,
    PRESETS,
    SETTINGS
}

data class MainUiState(
    val currentPage: MainPage = MainPage.DASHBOARD
)

class MainViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    fun navigateToPage(page: MainPage) {
        _uiState.value = _uiState.value.copy(currentPage = page)
    }
}