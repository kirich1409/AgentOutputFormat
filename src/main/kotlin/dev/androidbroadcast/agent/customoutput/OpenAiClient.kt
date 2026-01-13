package dev.androidbroadcast.agent.customoutput

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val DEFAULT_BASE_URL = "https://api.artemox.com/v1"
private const val DEFAULT_MODEL = "gpt-4o-mini"

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ResponseFormat(
    val type: String
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    @SerialName("response_format")
    val responseFormat: ResponseFormat? = ResponseFormat("json_object")
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class Post(
    val title: String,
    val body: String,
    val tags: List<String>,
    @SerialName("when")
    val whenUtc: String
)

class OpenAiClient(
    private val baseUrl: String = System.getenv("OPENAI_BASE_URL")?.ifBlank { null } ?: DEFAULT_BASE_URL,
    private val token: String = System.getenv("OPENAI_TOKEN")?.ifBlank { null }
        ?: error("OPENAI_TOKEN environment variable is required."),
    private val model: String = System.getenv("OPENAI_MODEL")?.ifBlank { null } ?: DEFAULT_MODEL
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    suspend fun generatePost(topic: String): Post {
        val systemPrompt = buildString {
            appendLine("You are a helpful assistant that writes Telegram posts in Russian.")
            appendLine("Return strictly a JSON object with fields:")
            appendLine("\"title\": String, \"body\": String, \"tags\": [String], \"when\": String.")
            appendLine("\"when\" must be the UTC time when the request is received, in ISO-8601 format.")
            appendLine("No extra fields, no markdown, no surrounding text.")
            appendLine("Body length must be <= 200 characters (excluding title and tags).")
        }

        val request = ChatRequest(
            model = model,
            messages = listOf(
                Message("system", systemPrompt.trim()),
                Message("user", "Create a Telegram post on the topic: $topic.")
            )
        )

        val response = client.post("$baseUrl/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(request)
        }.body<ChatResponse>()

        val content = response.choices.firstOrNull()?.message?.content
            ?: error("No content in response.")

        val post = try {
            json.decodeFromString(Post.serializer(), content.trim())
        } catch (e: Exception) {
            error("Response is not valid JSON: ${e.message}")
        }

        if (post.body.length > 200) {
            error("Body exceeds 200 characters.")
        }

        return post
    }

    fun encodePost(post: Post): String = json.encodeToString(Post.serializer(), post)
}
