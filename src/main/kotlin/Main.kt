package org.example

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        throw IllegalArgumentException("Please specify the markdown file path.")
    }
    val filePath = args[0]

    ConfigLoader.loadYml("application.yml")

    val service = MarkdownToSlackService()
    service.postToSlack("sample-file/${filePath}")
}