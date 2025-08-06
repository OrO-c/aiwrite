package com.aiwriter.assistant.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.data.model.WritingPreset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PresetsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PresetsViewModel = viewModel()
) {
    val presets by viewModel.presets.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPreset by remember { mutableStateOf<WritingPreset?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预设管理") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加预设")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presets) { preset ->
                PresetCard(
                    preset = preset,
                    onEdit = { editingPreset = preset },
                    onDelete = { viewModel.deletePreset(preset) },
                    onSetDefault = { viewModel.setDefaultPreset(preset.id) }
                )
            }
        }
    }
    
    if (showAddDialog) {
        PresetEditDialog(
            preset = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, description, prompt ->
                viewModel.createPreset(name, description, prompt)
                showAddDialog = false
            }
        )
    }
    
    editingPreset?.let { preset ->
        PresetEditDialog(
            preset = preset,
            onDismiss = { editingPreset = null },
            onSave = { name, description, prompt ->
                viewModel.updatePreset(preset.copy(
                    name = name,
                    description = description,
                    systemPrompt = prompt
                ))
                editingPreset = null
            }
        )
    }
}

@Composable
private fun PresetCard(
    preset: WritingPreset,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (preset.isDefault) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (preset.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge { Text("默认") }
                        }
                    }
                    
                    Text(
                        text = preset.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row {
                    if (!preset.isDefault) {
                        IconButton(onClick = onSetDefault) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "设为默认",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "编辑",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "系统提示: ${preset.systemPrompt.take(100)}${if (preset.systemPrompt.length > 100) "..." else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PresetEditDialog(
    preset: WritingPreset?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(preset?.name ?: "") }
    var description by remember { mutableStateOf(preset?.description ?: "") }
    var systemPrompt by remember { mutableStateOf(preset?.systemPrompt ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (preset == null) "添加预设" else "编辑预设")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("预设名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                
                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("系统提示词") },
                    placeholder = { Text("请确保包含 /FGX/ 分隔指令") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && systemPrompt.isNotBlank()) {
                        onSave(name, description, systemPrompt)
                    }
                },
                enabled = name.isNotBlank() && systemPrompt.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}