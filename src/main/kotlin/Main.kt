package org.example

fun main() {
    val filePath = System.getenv("MARKDOWN_FILE_PATH")
    val service = MarkdownToSlackService()
    service.postToSlack(filePath)
}