package com.aiwriter.assistant.data.repository

import com.aiwriter.assistant.data.database.dao.WritingPresetDao
import com.aiwriter.assistant.data.model.WritingPreset
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PresetRepository(private val presetDao: WritingPresetDao) {
    
    fun getAllPresets(): Flow<List<WritingPreset>> = presetDao.getAllPresets()
    
    suspend fun getPresetById(id: String): WritingPreset? = presetDao.getPresetById(id)
    
    suspend fun getDefaultPreset(): WritingPreset? = presetDao.getDefaultPreset()
    
    suspend fun insertPreset(preset: WritingPreset) = presetDao.insertPreset(preset)
    
    suspend fun updatePreset(preset: WritingPreset) {
        val updatedPreset = preset.copy(updatedAt = System.currentTimeMillis())
        presetDao.updatePreset(updatedPreset)
    }
    
    suspend fun deletePreset(preset: WritingPreset) = presetDao.deletePreset(preset)
    
    suspend fun setDefaultPreset(presetId: String) {
        presetDao.clearDefaultFlags()
        val preset = presetDao.getPresetById(presetId)
        preset?.let {
            updatePreset(it.copy(isDefault = true))
        }
    }
    
    suspend fun initializeDefaultPresets() {
        val defaultPresets = getDefaultPresetList()
        defaultPresets.forEach { preset ->
            insertPreset(preset)
        }
    }
    
    private fun getDefaultPresetList(): List<WritingPreset> {
        return listOf(
            WritingPreset(
                id = UUID.randomUUID().toString(),
                name = "小红书文案",
                description = "适合小红书的种草文案风格",
                systemPrompt = """
                    你是一个专业的小红书文案写手。请生成符合小红书风格的文案，要求：
                    1. 语言活泼生动，贴近年轻人
                    2. 适当使用表情符号
                    3. 突出产品特点和使用体验
                    4. 适合分享和互动
                """.trimIndent(),
                isDefault = true
            ),
            WritingPreset(
                id = UUID.randomUUID().toString(),
                name = "商务邮件",
                description = "正式的商务邮件模板",
                systemPrompt = """
                    你是一个专业的商务写作助手。请生成正式的商务邮件内容，要求：
                    1. 语言正式礼貌
                    2. 结构清晰条理
                    3. 表达准确专业
                    4. 符合商务邮件规范
                """.trimIndent()
            ),
            WritingPreset(
                id = UUID.randomUUID().toString(),
                name = "产品评价",
                description = "客观中肯的产品评价",
                systemPrompt = """
                    你是一个客观公正的产品评测专家。请生成真实可信的产品评价，要求：
                    1. 客观描述产品特点
                    2. 分析优缺点
                    3. 给出使用建议
                    4. 语言自然真实
                """.trimIndent()
            ),
            WritingPreset(
                id = UUID.randomUUID().toString(),
                name = "日记助手",
                description = "个人日记和心情记录",
                systemPrompt = """
                    你是一个贴心的日记助手。请帮助用户记录日常生活和心情，要求：
                    1. 语言温暖亲切
                    2. 关注情感表达
                    3. 记录生活细节
                    4. 鼓励积极思考
                """.trimIndent()
            ),
            WritingPreset(
                id = UUID.randomUUID().toString(),
                name = "学习笔记",
                description = "整理和总结学习内容",
                systemPrompt = """
                    你是一个高效的学习助手。请帮助用户整理学习内容，要求：
                    1. 结构化整理知识点
                    2. 突出重点难点
                    3. 便于记忆和复习
                    4. 逻辑清晰准确
                """.trimIndent()
            )
        )
    }
}