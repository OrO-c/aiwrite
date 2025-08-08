package com.aiwriter.assistant.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwriter.assistant.data.model.GeneratedText
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.utils.PermissionHelper
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToPresets: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val recentTexts by viewModel.recentTexts.collectAsState()
    val workMode by viewModel.workMode.collectAsState()
    val missingPermissions by viewModel.missingPermissions.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 写作助手") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::startNewGeneration,
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("新建生成") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Work mode status card
            item {
                WorkModeStatusCard(
                    workMode = workMode,
                    missingPermissions = missingPermissions,
                    onRequestPermissions = {
                        val activity = context as? Activity ?: return@WorkModeStatusCard
                        when {
                            missingPermissions.contains("悬浮窗权限") -> {
                                PermissionHelper.requestOverlayPermission(activity)
                            }
                            missingPermissions.contains("无障碍服务") -> {
                                PermissionHelper.requestAccessibilityPermission(activity)
                            }
                            missingPermissions.contains("通知权限") -> {
                                PermissionHelper.requestNotificationPermission(activity)
                            }
                        }
                    },
                    onChangeMode = { onNavigateToSettings() }
                )
            }
            
            // Quick actions
            item {
                QuickActionsCard(
                    onManagePresets = onNavigateToPresets,
                    onSettings = onNavigateToSettings
                )
            }
            
            // Recent texts section
            item {
                Text(
                    text = "最近生成",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (recentTexts.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(recentTexts) { text ->
                    RecentTextCard(
                        text = text,
                        onReload = { viewModel.regenerateText(text) },
                        onDelete = { viewModel.deleteText(text) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkModeStatusCard(
    workMode: WorkMode,
    missingPermissions: List<String>,
    onRequestPermissions: () -> Unit,
    onChangeMode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (missingPermissions.isEmpty()) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
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
                    Text(
                        text = "当前模式: ${workMode.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (missingPermissions.isEmpty()) {
                        Text(
                            text = "✅ 已就绪",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "⚠️ 缺少权限: ${missingPermissions.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                IconButton(onClick = onChangeMode) {
                    Icon(Icons.Default.Settings, contentDescription = "更改模式")
                }
            }
            
            if (missingPermissions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("申请权限")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onManagePresets: () -> Unit,
    onSettings: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快捷操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    label = "管理预设",
                    onClick = onManagePresets,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                QuickActionButton(
                    icon = Icons.Default.Tune,
                    label = "模型设置",
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "还没有生成过文本",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "点击右下角的按钮开始你的第一次写作",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RecentTextCard(
    text: GeneratedText,
    onReload: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = text.presetName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormat.format(Date(text.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onReload) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "重新生成",
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
            
            // Input
            Text(
                text = "输入: ${text.input}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Preview
            Text(
                text = text.version1.take(80) + if (text.version1.length > 80) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}