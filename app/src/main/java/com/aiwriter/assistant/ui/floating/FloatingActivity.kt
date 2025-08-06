package com.aiwriter.assistant.ui.floating

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.service.AccessibilityService
import com.aiwriter.assistant.ui.theme.AIWritingAssistantTheme

class FloatingActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val mode = intent.getStringExtra("mode") ?: "floating"
        
        setContent {
            AIWritingAssistantTheme {
                FloatingWritingInterface(
                    mode = mode,
                    onClose = { finish() }
                )
            }
        }
    }
}

@Composable
fun FloatingWritingInterface(
    mode: String,
    onClose: () -> Unit,
    viewModel: FloatingViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI 写作助手",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content based on current state
                when {
                    uiState.isLoading -> {
                        LoadingState()
                    }
                    uiState.generatedText != null -> {
                        val generatedText = uiState.generatedText!!
                        GeneratedTextDisplay(
                            generatedText = generatedText,
                            mode = mode,
                            onCopyText = { text ->
                                copyToClipboard(context, text)
                                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                            },
                            onInsertText = { text ->
                                if (mode == "floating" && AccessibilityService.isServiceEnabled()) {
                                    val success = AccessibilityService.insertText(text)
                                    if (success) {
                                        Toast.makeText(context, "文本已插入", Toast.LENGTH_SHORT).show()
                                        onClose()
                                    } else {
                                        copyToClipboard(context, text)
                                        Toast.makeText(context, "插入失败，已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    copyToClipboard(context, text)
                                    Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRegenerate = viewModel::regenerateText,
                            onNewGeneration = viewModel::resetToInput
                        )
                    }
                    else -> {
                        InputInterface(
                            currentPreset = uiState.currentPreset,
                            inputText = uiState.inputText,
                            onInputChanged = viewModel::updateInputText,
                            onPresetChanged = viewModel::selectPreset,
                            onGenerate = viewModel::generateText,
                            presets = uiState.availablePresets
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "正在生成文本...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun InputInterface(
    currentPreset: String,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onPresetChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    presets: List<com.aiwriter.assistant.data.model.WritingPreset>
) {
    Column {
        // Preset selection
        var expanded by remember { mutableStateOf(false) }
        
        Box {
            OutlinedTextField(
                value = currentPreset,
                onValueChange = {},
                readOnly = true,
                label = { Text("选择预设") },
                trailingIcon = { 
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "展开"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
            )
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { preset ->
                    DropdownMenuItem(
                        text = { Text(preset.name) },
                        onClick = {
                            onPresetChanged(preset.name)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input field
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChanged,
            label = { Text("请输入主题...") },
            placeholder = { Text("例如：防晒霜测评") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Generate button
        Button(
            onClick = onGenerate,
            enabled = inputText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("生成文本")
        }
    }
}

@Composable
private fun GeneratedTextDisplay(
    generatedText: com.aiwriter.assistant.data.model.GeneratedText,
    mode: String,
    onCopyText: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onRegenerate: () -> Unit,
    onNewGeneration: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        // Header with actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "生成结果",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                IconButton(onClick = onRegenerate) {
                    Icon(Icons.Default.Refresh, contentDescription = "重新生成")
                }
                IconButton(onClick = onNewGeneration) {
                    Icon(Icons.Default.Add, contentDescription = "新建生成")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Generated text versions
        TextVersionCard(
            label = generatedText.style1Label,
            text = generatedText.version1,
            mode = mode,
            onCopy = { onCopyText(generatedText.version1) },
            onInsert = { onInsertText(generatedText.version1) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextVersionCard(
            label = generatedText.style2Label,
            text = generatedText.version2,
            mode = mode,
            onCopy = { onCopyText(generatedText.version2) },
            onInsert = { onInsertText(generatedText.version2) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextVersionCard(
            label = generatedText.style3Label,
            text = generatedText.version3,
            mode = mode,
            onCopy = { onCopyText(generatedText.version3) },
            onInsert = { onInsertText(generatedText.version3) }
        )
    }
}

@Composable
private fun TextVersionCard(
    label: String,
    text: String,
    mode: String,
    onCopy: () -> Unit,
    onInsert: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "复制",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    if (mode == "floating") {
                        IconButton(onClick = onInsert) {
                            Icon(
                                Icons.Default.Input,
                                contentDescription = "插入",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AI Writing Assistant", text)
    clipboard.setPrimaryClip(clip)
}