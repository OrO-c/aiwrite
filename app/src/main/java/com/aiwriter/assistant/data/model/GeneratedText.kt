package com.aiwriter.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_texts")
data class GeneratedText(
    @PrimaryKey val id: String,
    val input: String,
    val presetId: String,
    val presetName: String,
    val version1: String,
    val version2: String,
    val version3: String,
    val style1Label: String = "💡 创意版",
    val style2Label: String = "🔍 详细版", 
    val style3Label: String = "✂️ 简洁版",
    val modelProvider: String,
    val createdAt: Long = System.currentTimeMillis()
)