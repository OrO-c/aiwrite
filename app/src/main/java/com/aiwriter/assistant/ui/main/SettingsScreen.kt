package com.aiwriter.assistant.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.aiwriter.assistant.ui.onboarding.OnboardingActivity
import com.aiwriter.assistant.data.model.ApiProvider
import com.aiwriter.assistant.data.model.WorkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    BackHandler { onNavigateBack() }
    val uiState by viewModel.uiState.collectAsState()
    var showApiDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Work Mode Section
            SettingsSection(title = "工作模式") {
                WorkModeSelection(
                    currentMode = uiState.workMode,
                    onModeChanged = { mode ->
                        viewModel.updateWorkMode(mode)
                        // After switching mode, guide user to permissions setup
                        val intent = Intent(context, OnboardingActivity::class.java).apply {
                            putExtra("startAt", "permissions")
                        }
                        context.startActivity(intent)
                    }
                )
            }
            
            // API Configuration Section
            SettingsSection(title = "AI 模型配置") {
                ApiConfigurationSection(
                    currentProvider = uiState.currentApiProvider,
                    apiConfigs = uiState.apiConfigs,
                    onProviderChanged = viewModel::updateCurrentProvider,
                    onConfigChanged = viewModel::updateApiConfig,
                    onTestConnection = viewModel::testConnection,
                    onOpenDialog = { showApiDialog = true }
                )
            }
            
            // Preset management entry tip
            SettingsSection(title = "预设提示") {
                Text(
                    text = "自定义系统提示词时，如需生成多段结果用于对比，请在提示词中明确使用 /FGX/ 作为分隔指令（例如：按顺序输出三段内容并用 /FGX/ 分隔）。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // App Settings Section
            SettingsSection(title = "应用设置") {
                AppSettingsSection(
                    isDarkMode = uiState.isDarkMode,
                    isVibrationEnabled = uiState.isVibrationEnabled,
                    onDarkModeChanged = viewModel::updateDarkMode,
                    onVibrationChanged = viewModel::updateVibration
                )
            }
            
            // Data Management Section
            SettingsSection(title = "数据管理") {
                DataManagementSection(
                    onClearHistory = viewModel::clearHistory,
                    onResetApp = viewModel::resetApp
                )
            }
        }
    }
    
    if (showApiDialog) {
        ApiConfigDialog(
            provider = uiState.currentApiProvider,
            existing = uiState.apiConfigs[uiState.currentApiProvider],
            onDismiss = { showApiDialog = false },
            onSave = { config ->
                viewModel.updateApiConfig(config)
                showApiDialog = false
            },
            onTest = { config -> viewModel.testConnection(config) }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun WorkModeSelection(
    currentMode: WorkMode,
    onModeChanged: (WorkMode) -> Unit
) {
    Column {
        WorkMode.values().forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentMode == mode,
                        onClick = { onModeChanged(mode) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentMode == mode,
                    onClick = { onModeChanged(mode) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = mode.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ApiConfigurationSection(
    currentProvider: ApiProvider,
    apiConfigs: Map<ApiProvider, com.aiwriter.assistant.data.model.ApiConfig>,
    onProviderChanged: (ApiProvider) -> Unit,
    onConfigChanged: (com.aiwriter.assistant.data.model.ApiConfig) -> Unit,
    onTestConnection: (com.aiwriter.assistant.data.model.ApiConfig) -> Unit,
    onOpenDialog: () -> Unit
) {
    Column {
        Text(
            text = "当前提供商",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ApiProvider.values().forEach { provider ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentProvider == provider,
                        onClick = { onProviderChanged(provider) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentProvider == provider,
                    onClick = { onProviderChanged(provider) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = provider.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                apiConfigs[provider]?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "已配置",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onOpenDialog,
            modifier = Modifier.fillMaxWidth()
        ) { Text("配置 ${currentProvider.displayName}") }
    }
}

@Composable
private fun ApiConfigDialog(
    provider: ApiProvider,
    existing: com.aiwriter.assistant.data.model.ApiConfig?,
    onDismiss: () -> Unit,
    onSave: (com.aiwriter.assistant.data.model.ApiConfig) -> Unit,
    onTest: (com.aiwriter.assistant.data.model.ApiConfig) -> Unit
) {
    var apiKey by remember { mutableStateOf(existing?.apiKey ?: "") }
    var endpoint by remember { mutableStateOf(existing?.endpoint ?: provider.defaultEndpoint) }
    var model by remember { mutableStateOf(existing?.model ?: when(provider){ ApiProvider.OPENAI->"gpt-3.5-turbo"; ApiProvider.DEEPSEEK->"deepseek-chat"; ApiProvider.GEMINI->"gemini-pro"; ApiProvider.CUSTOM->"" }) }
    var temperature by remember { mutableStateOf(existing?.temperature ?: 0.7f) }
    var topP by remember { mutableStateOf(existing?.topP ?: 1.0f) }
    var maxTokens by remember { mutableStateOf(existing?.maxTokens ?: 2000) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("配置 ${provider.displayName}") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = apiKey, onValueChange = { apiKey = it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                if (provider == ApiProvider.CUSTOM) {
                    OutlinedTextField(value = endpoint, onValueChange = { endpoint = it }, label = { Text("Endpoint") }, modifier = Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("模型") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "温度 ${"%.2f".format(temperature)}")
                Slider(value = temperature, onValueChange = { temperature = it }, valueRange = 0f..1f)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Top-p ${"%.2f".format(topP)}")
                Slider(value = topP, onValueChange = { topP = it }, valueRange = 0f..1f)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = maxTokens.toString(), onValueChange = { it.toIntOrNull()?.let { v -> maxTokens = v } }, label = { Text("Max tokens") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val cfg = com.aiwriter.assistant.data.model.ApiConfig(
                    provider = provider,
                    apiKey = apiKey,
                    endpoint = endpoint,
                    model = model,
                    temperature = temperature,
                    topP = topP,
                    maxTokens = maxTokens
                )
                onSave(cfg)
            }) { Text("保存") }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    val cfg = com.aiwriter.assistant.data.model.ApiConfig(
                        provider = provider,
                        apiKey = apiKey,
                        endpoint = endpoint,
                        model = model,
                        temperature = temperature,
                        topP = topP,
                        maxTokens = maxTokens
                    )
                    onTest(cfg)
                }) { Text("测试连接") }
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        }
    )
}

@Composable
private fun AppSettingsSection(
    isDarkMode: Boolean,
    isVibrationEnabled: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "夜间模式",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChanged
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "震动反馈",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isVibrationEnabled,
                onCheckedChange = onVibrationChanged
            )
        }
    }
}

@Composable
private fun DataManagementSection(
    onClearHistory: () -> Unit,
    onResetApp: () -> Unit
) {
    var showClearDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    
    Column {
        OutlinedButton(
            onClick = { showClearDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("清除历史记录")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("重置应用")
        }
    }
    
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("确认清除") },
            text = { Text("这将删除所有生成的文本历史记录，此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("确认重置") },
            text = { Text("这将删除所有数据并重置应用到初始状态，此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetApp()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("确认重置")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}