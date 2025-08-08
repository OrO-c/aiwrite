package com.aiwriter.assistant.ui.onboarding

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.utils.PermissionHelper

@Composable
fun PermissionSetupPage(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val selectedMode by viewModel.selectedWorkMode

    // Live permission states (re-evaluated on each composition)
    val hasOverlay = PermissionHelper.hasOverlayPermission(context)
    val hasAccessibility = PermissionHelper.isAccessibilityServiceEnabled(context)
    val hasNotification = PermissionHelper.hasNotificationPermission(context)

    // Determine required permissions by mode
    val required = when (selectedMode) {
        WorkMode.TILE_CLIPBOARD -> listOf(
            PermissionItem(
                title = "通知权限",
                description = "用于显示快速设置磁贴，方便快速唤起写作助手。",
                isGranted = hasNotification,
                onRequest = { activity?.let { PermissionHelper.requestNotificationPermission(it) } }
            )
        )
        WorkMode.FLOATING_INPUT -> listOf(
            PermissionItem(
                title = "悬浮窗权限",
                description = "用于显示悬浮球与写作界面，实现一键直填。",
                isGranted = hasOverlay,
                onRequest = { activity?.let { PermissionHelper.requestOverlayPermission(it) } }
            ),
            PermissionItem(
                title = "无障碍服务",
                description = "用于将生成的文本直接插入到其他应用的输入框。",
                isGranted = hasAccessibility,
                onRequest = { activity?.let { PermissionHelper.requestAccessibilityPermission(it) } }
            )
        )
    }

    val allGranted = required.all { it.isGranted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "获取必要权限",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "当前模式：${selectedMode.displayName}。请依次开启以下权限，以获得最佳体验。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Permission list
        required.forEach { item ->
            PermissionCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onNext,
            enabled = allGranted,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) { Text(if (allGranted) "继续" else "请先完成权限开启") }
    }
}

@Composable
private fun PermissionCard(item: PermissionItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (item.isGranted) {
                    AssistChip(onClick = {}, label = { Text("已开启") }, enabled = false)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))
            if (!item.isGranted) {
                Button(onClick = item.onRequest, modifier = Modifier.fillMaxWidth()) {
                    Text("去开启")
                }
            }
        }
    }
}

private data class PermissionItem(
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val onRequest: () -> Unit
)