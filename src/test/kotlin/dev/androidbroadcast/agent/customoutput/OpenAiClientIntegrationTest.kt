package dev.androidbroadcast.agent.customoutput

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class OpenAiClientIntegrationTest {
    @Test
    fun `generates valid JSON posts for IT topics`() = runBlocking {
        val client = OpenAiClient()
        val topics = (1..3).map { "IT topic $it" }

        topics.forEach { topic ->
            val post = client.generatePost(topic)
            assertTrue(post.title.isNotBlank(), "Title should not be blank for topic: $topic")
            assertTrue(post.body.isNotBlank(), "Body should not be blank for topic: $topic")
            assertTrue(post.body.length <= 200, "Body exceeds 200 chars for topic: $topic")
            assertTrue(post.tags.isNotEmpty(), "Tags should not be empty for topic: $topic")
            assertTrue(post.tags.all { it.isNotBlank() }, "Tags should not be blank for topic: $topic")
            assertTrue(post.whenUtc.isNotBlank(), "When should not be blank for topic: $topic")
        }
    }
}
