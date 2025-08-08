package com.aiwriter.assistant.data.repository

import com.aiwriter.assistant.data.api.ChatCompletionRequest
import com.aiwriter.assistant.data.api.ChatCompletionResponse
import com.aiwriter.assistant.data.api.ChatMessage
import com.aiwriter.assistant.data.api.OpenAIService
import com.aiwriter.assistant.data.model.ApiConfig
import com.aiwriter.assistant.data.model.ApiProvider
import com.aiwriter.assistant.data.model.GeneratedText
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class AITextRepository {
    
    private val openAIService = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenAIService::class.java)
    
    suspend fun generateThreeVersions(
        input: String,
        systemPrompt: String,
        apiConfig: ApiConfig
    ): Result<Triple<String, String, String>> {
        return try {
            when (apiConfig.provider) {
                ApiProvider.OPENAI, ApiProvider.DEEPSEEK, ApiProvider.CUSTOM -> {
                    generateWithOpenAIFormat(input, systemPrompt, apiConfig)
                }
                ApiProvider.GEMINI -> {
                    generateWithGemini(input, systemPrompt, apiConfig)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun generateWithOpenAIFormat(
        input: String,
        systemPrompt: String,
        apiConfig: ApiConfig
    ): Result<Triple<String, String, String>> {
        val fullPrompt = """
            $systemPrompt
            
            请根据以下主题生成精炼、可直接用于输入框的一段文本：
            
            主题：$input
            
            要求：
            - 语言清晰、自然，尽量减少多余铺陈
            - 可直接复制粘贴或一键插入
            - 若有必要，可适度分句，便于快速浏览
            
            格式：
            版本1内容
            /FGX/
            版本2内容
            /FGX/
            版本3内容
        """.trimIndent()
        
        val messages = listOf(
            ChatMessage("system", "你是一个专业的写作助手。"),
            ChatMessage("user", fullPrompt)
        )
        
        val request = ChatCompletionRequest(
            model = apiConfig.model,
            messages = messages,
            temperature = apiConfig.temperature,
            max_tokens = apiConfig.maxTokens
        )
        
        val response = openAIService.generateText(
            url = apiConfig.endpoint,
            authorization = "Bearer ${apiConfig.apiKey}",
            request = request
        )
        
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("没有收到AI响应")
        
        val versions = content.split("/FGX/").map { it.trim() }
        if (versions.size < 3) {
            throw Exception("AI响应格式不正确，未能生成3个版本")
        }
        
        return Result.success(Triple(versions[0], versions[1], versions[2]))
    }
    
    private suspend fun generateWithGemini(
        input: String,
        systemPrompt: String,
        apiConfig: ApiConfig
    ): Result<Triple<String, String, String>> {
        // Gemini API implementation would go here
        // For now, return a placeholder
        return Result.success(Triple(
            "$input（Gemini暂未实现）",
            "",
            ""
        ))
    }
    
    suspend fun testApiConnection(apiConfig: ApiConfig): Result<String> {
        return try {
            val testPrompt = "请回复'连接成功'"
            val result = generateWithOpenAIFormat(
                input = testPrompt,
                systemPrompt = "简短回复测试。",
                apiConfig = apiConfig.copy(maxTokens = 50)
            )
            
            if (result.isSuccess) {
                Result.success("API连接成功")
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("连接失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}