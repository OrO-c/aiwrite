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
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

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
        android.util.Log.d("AITextRepository", "generateWithOpenAIFormat called")
        android.util.Log.d("AITextRepository", "Input: $input")
        android.util.Log.d("AITextRepository", "API Config: ${apiConfig.provider}, ${apiConfig.endpoint}")
        
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
            top_p = apiConfig.topP,
            max_tokens = apiConfig.maxTokens
        )
        
        android.util.Log.d("AITextRepository", "Making API request...")
        
        return try {
            val response = withTimeout(15_000L) {
                openAIService.generateText(
                    url = apiConfig.endpoint,
                    authorization = "Bearer ${apiConfig.apiKey}",
                    request = request
                )
            }
            
            android.util.Log.d("AITextRepository", "API response received")
            
            val content = response.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("没有收到AI响应"))
            
            android.util.Log.d("AITextRepository", "Content length: ${content.length}")
            
            val versions = content.split("/FGX/").map { it.trim() }
            val v1 = versions.getOrNull(0) ?: ""
            val v2 = versions.getOrNull(1) ?: ""
            val v3 = versions.getOrNull(2) ?: ""
            
            if (v1.isBlank()) {
                return Result.failure(Exception("AI响应内容为空"))
            }
            
            android.util.Log.d("AITextRepository", "Generation successful")
            Result.success(Triple(v1, v2, v3))
        } catch (e: TimeoutCancellationException) {
            android.util.Log.e("AITextRepository", "Request timeout", e)
            Result.failure(Exception("请求超时，请稍后重试"))
        } catch (e: retrofit2.HttpException) {
            android.util.Log.e("AITextRepository", "HTTP error: ${e.code()}", e)
            val errorMessage = when (e.code()) {
                401 -> "API密钥无效，请检查配置"
                403 -> "API密钥权限不足"
                429 -> "请求过于频繁，请稍后重试"
                500 -> "服务器内部错误"
                else -> "网络请求失败 (${e.code()})"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: java.net.UnknownHostException) {
            android.util.Log.e("AITextRepository", "Network error", e)
            Result.failure(Exception("网络连接失败，请检查网络设置"))
        } catch (e: java.net.SocketTimeoutException) {
            android.util.Log.e("AITextRepository", "Socket timeout", e)
            Result.failure(Exception("连接超时，请检查网络"))
        } catch (e: Exception) {
            android.util.Log.e("AITextRepository", "Unexpected error", e)
            Result.failure(Exception("生成失败: ${e.message}"))
        }
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