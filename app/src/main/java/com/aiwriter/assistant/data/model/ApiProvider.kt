package com.aiwriter.assistant.data.model

enum class ApiProvider(val displayName: String, val defaultEndpoint: String) {
    OPENAI("OpenAI", "https://api.openai.com/v1/chat/completions"),
    DEEPSEEK("DeepSeek", "https://api.deepseek.com/v1/chat/completions"),
    GEMINI("Gemini", "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"),
    CUSTOM("自定义", "")
}

data class ApiConfig(
    val provider: ApiProvider,
    val apiKey: String,
    val endpoint: String = provider.defaultEndpoint,
    val model: String = when(provider) {
        ApiProvider.OPENAI -> "gpt-3.5-turbo"
        ApiProvider.DEEPSEEK -> "deepseek-chat"
        ApiProvider.GEMINI -> "gemini-pro"
        ApiProvider.CUSTOM -> ""
    },
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2000,
    val streamResponse: Boolean = false
)