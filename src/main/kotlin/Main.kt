package org.example

fun main(args: Array<String>) {
    ConfigLoader.loadYml("config.yml")
    val filePath = if (args.isNotEmpty()) args[0] else null
    if (filePath != null) {
        val service = MarkdownToSlackService()
        service.postToSlack(filePath)
    }else {
        println("Please specify the file path.")
    }
}
