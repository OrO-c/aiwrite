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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.service.AppAccessibilityService
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

    // Container with slide animations
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        val cornerRadius = 20.dp
        val targetHeightFraction = if (mode == "tile") 0.5f else 0.3f
        val enter = if (mode == "tile") {
            slideInVertically(initialOffsetY = { it }, animationSpec = tween(250)) + fadeIn(tween(200))
        } else {
            slideInVertically(initialOffsetY = { -it }, animationSpec = tween(250)) + fadeIn(tween(200))
        }
        val exit = if (mode == "tile") {
            slideOutVertically(targetOffsetY = { it }, animationSpec = tween(200)) + fadeOut(tween(150))
        } else {
            slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(200)) + fadeOut(tween(150))
        }

        AnimatedVisibility(visible = true, enter = enter, exit = exit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(targetHeightFraction)
                    .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
                    .padding(horizontal = 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: title centered, close on the right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(40.dp))
                        Text(
                            text = uiState.inputText.ifBlank { "AI 写作" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "关闭") }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    when {
                        uiState.isLoading -> LoadingState()
                        uiState.generatedText != null -> {
                            val generated = uiState.generatedText!!
                            GeneratedCompactDisplay(
                                title = generated.input,
                                text = generated.version1.ifBlank { "(无内容)" },
                                mode = mode,
                                onCopyText = {
                                    copyToClipboard(context, generated.version1)
                                    Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                },
                                onInsertText = {
                                    if (mode == "floating" && AppAccessibilityService.isServiceEnabled()) {
                                        val success = AppAccessibilityService.insertText(generated.version1)
                                        if (success) {
                                            Toast.makeText(context, "文本已插入", Toast.LENGTH_SHORT).show()
                                            onClose()
                                        } else {
                                            copyToClipboard(context, generated.version1)
                                            Toast.makeText(context, "插入失败，已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        copyToClipboard(context, generated.version1)
                                        Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onRegenerate = viewModel::regenerateText,
                                onNewGeneration = viewModel::resetToInput
                            )
                        }
                        else -> {
                            InputCompactInterface(
                                currentPreset = uiState.currentPreset,
                                inputText = uiState.inputText,
                                onInputChanged = viewModel::updateInputText,
                                onPresetChanged = viewModel::selectPreset,
                                onGenerate = viewModel::generateText,
                                presets = uiState.availablePresets,
                                error = uiState.error,
                                onTestApi = viewModel::testApiConnection
                            )
                        }
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "正在生成...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InputCompactInterface(
    currentPreset: String,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onPresetChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    presets: List<com.aiwriter.assistant.data.model.WritingPreset>,
    error: String?,
    onTestApi: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Preset dropdown with better hit target
        var expanded by remember { mutableStateOf(false) }
        val dropdownExpanded = remember { mutableStateOf(false) }
        
        Box {
            OutlinedTextField(
                value = currentPreset.ifBlank { "选择预设" },
                onValueChange = {},
                readOnly = true,
                label = { Text("预设") },
                trailingIcon = {
                    Icon(
                        if (dropdownExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "展开"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        dropdownExpanded.value = !dropdownExpanded.value
                        expanded = dropdownExpanded.value
                    }
            )
            
            DropdownMenu(
                expanded = dropdownExpanded.value, 
                onDismissRequest = { 
                    dropdownExpanded.value = false
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { preset ->
                    DropdownMenuItem(
                        text = { Text(preset.name) }, 
                        onClick = {
                            onPresetChanged(preset.name)
                            dropdownExpanded.value = false
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChanged,
            label = { Text("主题") },
            placeholder = { Text("如：防晒霜测评") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        if (!error.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onGenerate, 
                enabled = inputText.isNotBlank(), 
                modifier = Modifier.weight(1f)
            ) {
                Text("生成")
            }
            
            TextButton(
                onClick = onTestApi,
                modifier = Modifier.weight(0.5f)
            ) {
                Text("测试API")
            }
        }
    }
}

@Composable
private fun GeneratedCompactDisplay(
    title: String,
    text: String,
    mode: String,
    onCopyText: () -> Unit,
    onInsertText: () -> Unit,
    onRegenerate: () -> Unit,
    onNewGeneration: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text(text = text, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCopyText, modifier = Modifier.weight(1f)) { Text("复制") }
            if (mode == "floating") {
                Button(onClick = onInsertText, modifier = Modifier.weight(1f)) { Text("插入") }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onRegenerate, modifier = Modifier.weight(1f)) { Text("重新生成") }
            TextButton(onClick = onNewGeneration, modifier = Modifier.weight(1f)) { Text("新建") }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("AI Writing Assistant", text)
    clipboard.setPrimaryClip(clip)
}