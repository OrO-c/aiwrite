package com.aiwriter.assistant.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiwriter.assistant.data.model.ApiProvider

@Composable
fun ApiConfigurationPage(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val selectedProvider by viewModel.selectedApiProvider
    val apiKey by viewModel.apiKey
    val customEndpoint by viewModel.customEndpoint
    val isTestingConnection by viewModel.isTestingConnection
    val testResult by viewModel.connectionTestResult
    
    var showApiKey by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Icon(
            imageVector = Icons.Default.Api,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "配置 AI 模型",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "选择并配置至少一个 AI 服务提供商",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Provider selection
        Text(
            text = "选择服务提供商",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        ApiProvider.values().forEach { provider ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedProvider == provider,
                    onClick = { viewModel.selectApiProvider(provider) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = provider.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // API Key input
        OutlinedTextField(
            value = apiKey,
            onValueChange = viewModel::updateApiKey,
            label = { Text("API 密钥") },
            placeholder = { 
                Text(
                    when (selectedProvider) {
                        ApiProvider.OPENAI -> "sk-..."
                        ApiProvider.DEEPSEEK -> "sk-..."
                        ApiProvider.GEMINI -> "AI..."
                        ApiProvider.CUSTOM -> "输入您的API密钥"
                    }
                )
            },
            visualTransformation = if (showApiKey) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showApiKey = !showApiKey }) {
                    Icon(
                        imageVector = if (showApiKey) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (showApiKey) "隐藏" else "显示"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Custom endpoint (if custom provider selected)
        if (selectedProvider == ApiProvider.CUSTOM) {
            OutlinedTextField(
                value = customEndpoint,
                onValueChange = viewModel::updateCustomEndpoint,
                label = { Text("API 端点") },
                placeholder = { Text("https://api.example.com/v1/chat/completions") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Test connection button
        Button(
            onClick = viewModel::testApiConnection,
            enabled = !isTestingConnection && apiKey.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isTestingConnection) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("测试连接中...")
            } else {
                Text("测试连接")
            }
        }
        
        // Test result
        testResult?.let { result ->
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.startsWith("✅")) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Help text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "如何获取 API 密钥？",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                when (selectedProvider) {
                    ApiProvider.OPENAI -> {
                        Text(
                            text = "1. 访问 platform.openai.com\n2. 注册/登录账户\n3. 进入 API Keys 页面\n4. 创建新的密钥",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    ApiProvider.DEEPSEEK -> {
                        Text(
                            text = "1. 访问 platform.deepseek.com\n2. 注册/登录账户\n3. 进入 API Keys 页面\n4. 创建新的密钥",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    ApiProvider.GEMINI -> {
                        Text(
                            text = "1. 访问 aistudio.google.com\n2. 创建项目\n3. 启用 Gemini API\n4. 创建 API 密钥",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    ApiProvider.CUSTOM -> {
                        Text(
                            text = "使用兼容 OpenAI 格式的 API 端点",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Next button
        Button(
            onClick = onNext,
            enabled = viewModel.isConfigurationValid(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("下一步")
        }
        
        if (!viewModel.isConfigurationValid()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "请先测试连接成功后再继续",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}