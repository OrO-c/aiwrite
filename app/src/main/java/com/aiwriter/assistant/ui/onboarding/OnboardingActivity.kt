package com.aiwriter.assistant.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.ui.main.MainActivity
import com.aiwriter.assistant.ui.theme.AIWritingAssistantTheme

class OnboardingActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AIWritingAssistantTheme {
                OnboardingScreen(
                    onComplete = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val context = LocalContext.current
    
    Scaffold { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> WelcomePage(
                    onNext = {
                        // Navigate to next page
                    }
                )
                1 -> WorkModeSelectionPage(
                    viewModel = viewModel,
                    onNext = {
                        // Navigate to next page
                    }
                )
                2 -> ApiConfigurationPage(
                    viewModel = viewModel,
                    onNext = {
                        // Navigate to next page
                    }
                )
                3 -> CompletionPage(
                    onComplete = {
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                )
            }
        }
    }
}