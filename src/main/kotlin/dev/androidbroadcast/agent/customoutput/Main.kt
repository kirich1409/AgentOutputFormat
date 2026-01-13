package dev.androidbroadcast.agent.customoutput

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val client = OpenAiClient()
    val argList = args.toList()
    val interactive = argList.contains("--interactive") || argList.contains("-i")
    val stdinMode = argList.contains("--stdin")
    val topicArgs = argList.filterNot { it == "--interactive" || it == "-i" }
        .filterNot { it == "--stdin" }

    val hasStdinData = try {
        System.`in`.available() > 0
    } catch (e: Exception) {
        false
    }

    if (stdinMode || (!interactive && topicArgs.isEmpty() && hasStdinData)) {
        val lines = System.`in`.bufferedReader().readLines()
        lines.map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { topic ->
                val post = client.generatePost(topic)
                println(client.encodePost(post))
            }
        return@runBlocking
    }

    val shouldRunInteractive = interactive || topicArgs.isEmpty()
    if (shouldRunInteractive) {
        System.err.println("Interactive mode. Enter a topic per line, or Ctrl+D to exit.")
        while (true) {
            val line = readLine() ?: break
            val topic = line.trim()
            if (topic.isBlank()) {
                continue
            }
            val post = client.generatePost(topic)
            println(client.encodePost(post))
        }
        return@runBlocking
    }

    val topic = topicArgs.joinToString(" ").trim()
    val post = client.generatePost(topic)
    println(client.encodePost(post))
}
