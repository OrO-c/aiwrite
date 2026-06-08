package com.aiwriter.assistant.data.repository

import com.aiwriter.assistant.data.api.ChatCompletionRequest
import com.aiwriter.assistant.data.api.ChatMessage
import com.aiwriter.assistant.data.api.OpenAIService
import com.aiwriter.assistant.data.model.ApiConfig
import com.aiwriter.assistant.data.model.ApiProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AITextRepository {

    private val jsonMediaType = "application/json".toMediaType()
    private val gson = Gson()

    companion object {
        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val openAIService = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(okHttpClient)
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

    private fun resolveModel(apiConfig: ApiConfig): String {
        return if (apiConfig.provider == ApiProvider.CUSTOM && apiConfig.customModel.isNotBlank()) {
            apiConfig.customModel
        } else {
            apiConfig.model
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
            model = resolveModel(apiConfig),
            messages = messages,
            temperature = apiConfig.temperature,
            max_tokens = apiConfig.maxTokens
        )

        return try {
            val response = withTimeout(120_000L) {
                openAIService.generateText(
                    url = apiConfig.endpoint,
                    authorization = "Bearer ${apiConfig.apiKey}",
                    request = request
                )
            }

            val content = response.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("没有收到AI响应"))

            val versions = content.split("/FGX/").map { it.trim() }
            val v1 = versions.getOrNull(0) ?: ""
            val v2 = versions.getOrNull(1) ?: ""
            val v3 = versions.getOrNull(2) ?: ""

            if (v1.isBlank()) {
                return Result.failure(Exception("AI响应内容为空"))
            }

            Result.success(Triple(v1, v2, v3))
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("请求超时，请稍后重试"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Gemini API data classes ---

    private data class GeminiRequest(
        val contents: List<GeminiContent>,
        val systemInstruction: GeminiContent? = null,
        @SerializedName("generationConfig")
        val generationConfig: GeminiGenerationConfig? = null
    )

    private data class GeminiContent(
        val parts: List<GeminiPart>
    )

    private data class GeminiPart(
        val text: String
    )

    private data class GeminiGenerationConfig(
        val temperature: Float = 0.7f,
        @SerializedName("maxOutputTokens")
        val maxOutputTokens: Int = 2000
    )

    private data class GeminiResponse(
        val candidates: List<GeminiCandidate>? = null
    )

    private data class GeminiCandidate(
        val content: GeminiContent? = null,
        @SerializedName("finishReason")
        val finishReason: String? = null
    )

    private suspend fun generateWithGemini(
        input: String,
        systemPrompt: String,
        apiConfig: ApiConfig
    ): Result<Triple<String, String, String>> {
        return try {
            val geminiRequest = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = input)))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(
                    temperature = apiConfig.temperature,
                    maxOutputTokens = apiConfig.maxTokens
                )
            )

            val requestBody = gson.toJson(geminiRequest).toRequestBody(jsonMediaType)
            val modelName = resolveModel(apiConfig).ifBlank { "gemini-pro" }
            val url = "https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Goog-Api-Key", apiConfig.apiKey)
                .build()

            val content = withTimeout(120_000L) {
                val httpResponse = okHttpClient.newCall(request).execute()
                httpResponse.use { resp ->
                    if (!resp.isSuccessful) {
                        val errorBody = resp.body?.string() ?: "Unknown error"
                        throw Exception("Gemini API error (${resp.code}): $errorBody")
                    }
                    resp.body?.string()
                        ?: throw Exception("Empty response from Gemini")
                }
            }

            val geminiResponse = gson.fromJson(content, GeminiResponse::class.java)
            val geminiContent = geminiResponse.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?: return Result.failure(Exception("没有收到AI响应"))
            val versions = content.split("/FGX/").map { it.trim() }
            val v1 = versions.getOrNull(0) ?: ""
            val v2 = versions.getOrNull(1) ?: ""
            val v3 = versions.getOrNull(2) ?: ""

            if (v1.isBlank()) {
                return Result.failure(Exception("AI响应内容为空"))
            }

            Result.success(Triple(v1, v2, v3))
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("请求超时，请稍后重试"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testApiConnection(apiConfig: ApiConfig): Result<String> {
        return try {
            if (apiConfig.provider == ApiProvider.GEMINI) {
                // Gemini simple ping — direct OkHttp call, no /FGX/ dependency
                val pingRequest = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = "回复'ok'")))
                    ),
                    generationConfig = GeminiGenerationConfig(maxOutputTokens = 10)
                )
                val requestBody = gson.toJson(pingRequest).toRequestBody(jsonMediaType)
                val modelName = apiConfig.model.ifBlank { "gemini-pro" }
                val url = "https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Goog-Api-Key", apiConfig.apiKey)
                    .build()

                val response = withTimeout(30_000L) {
                    okHttpClient.newCall(request).execute()
                }

                response.use {
                    if (it.isSuccessful) {
                        Result.success("API连接成功")
                    } else {
                        val errorBody = it.body?.string() ?: "Unknown"
                        Result.failure(Exception("HTTP ${it.code}: $errorBody"))
                    }
                }
            } else {
                // OpenAI-compatible simple ping — check connectivity, not full parsing
                val testMessages = listOf(ChatMessage("user", "回复'ok'"))
                val testRequest = ChatCompletionRequest(
                    model = resolveModel(apiConfig),
                    messages = testMessages,
                    max_tokens = 10
                )

                val response = withTimeout(30_000L) {
                    openAIService.generateText(
                        url = apiConfig.endpoint,
                        authorization = "Bearer ${apiConfig.apiKey}",
                        request = testRequest
                    )
                }

                if (response.choices.isNotEmpty()) {
                    Result.success("API连接成功")
                } else {
                    Result.failure(Exception("API响应为空"))
                }
            }
        } catch (e: TimeoutCancellationException) {
            Result.failure(Exception("连接超时，请检查网络和API地址"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}