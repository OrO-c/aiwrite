package com.aiwriter.assistant.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aiwriter.assistant.data.model.WorkMode
import com.aiwriter.assistant.utils.PermissionHelper

@Composable
fun WorkModeSelectionPage(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val selectedMode by viewModel.selectedWorkMode
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "选择工作模式",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "根据使用频率选择最适合的模式",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Mode selection cards
        WorkModeCard(
            mode = WorkMode.TILE_CLIPBOARD,
            isSelected = selectedMode == WorkMode.TILE_CLIPBOARD,
            onSelect = { viewModel.selectWorkMode(WorkMode.TILE_CLIPBOARD) },
            icon = "⚡",
            permissions = PermissionHelper.getMissingPermissions(context, false)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        WorkModeCard(
            mode = WorkMode.FLOATING_INPUT,
            isSelected = selectedMode == WorkMode.FLOATING_INPUT,
            onSelect = { viewModel.selectWorkMode(WorkMode.FLOATING_INPUT) },
            icon = "🖊️",
            permissions = PermissionHelper.getMissingPermissions(context, true)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Next button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("下一步")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "权限将在后续步骤中申请",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WorkModeCard(
    mode: WorkMode,
    isSelected: Boolean,
    onSelect: () -> Unit,
    icon: String,
    permissions: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(
                    MaterialTheme.colorScheme.primary
                )
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = mode.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect
                )
            }
            
            if (permissions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "需要权限: ${permissions.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Usage scenarios
            val scenarios = when (mode) {
                WorkMode.TILE_CLIPBOARD -> listOf(
                    "偶尔使用写作助手",
                    "注重手机续航",
                    "习惯复制粘贴操作"
                )
                WorkMode.FLOATING_INPUT -> listOf(
                    "频繁使用写作助手",
                    "需要快速输入",
                    "多应用间切换写作"
                )
            }
            
            Column {
                Text(
                    text = "适合场景:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                scenarios.forEach { scenario ->
                    Text(
                        text = "• $scenario",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }
        }
    }
}