package org.example

fun main() {
    ConfigLoader.loadYml("application.yml")

    val service = MarkdownToSlackService()
    service.postToSlack("sample-file/sample.md")
}