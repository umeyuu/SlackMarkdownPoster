package org.example

fun main() {
    ConfigLoader.loadYml("application.yml")
    val filePath = System.getenv("MARKDOWN_FILE_PATH")
    val service = MarkdownToSlackService()
    service.postToSlack(filePath)
}