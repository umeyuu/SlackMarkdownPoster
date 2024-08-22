package org.example

fun main() {
    val filePath = System.getenv("FILE_PATH")
    val service = MarkdownToSlackService()
    service.postToSlack(filePath)
}