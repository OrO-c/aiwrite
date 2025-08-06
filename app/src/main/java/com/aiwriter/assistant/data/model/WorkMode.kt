package com.aiwriter.assistant.data.model

enum class WorkMode(val displayName: String, val description: String) {
    TILE_CLIPBOARD("磁贴+剪贴板", "省电模式，点击磁贴生成文本到剪贴板"),
    FLOATING_INPUT("悬浮球+直接输入", "高效模式，直接输入到当前应用")
}