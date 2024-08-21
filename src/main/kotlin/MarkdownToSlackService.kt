package org.example

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import java.io.File
import java.util.*


class MarkdownToSlackService {

    fun postToSlack(filePath: String) {
        val document = parseToAst(filePath)
        val slackBlocks = convertToSlackBlocks(document)

        val slackApiClient = SlackApiClient()
        slackApiClient.postToSlack(slackBlocks)
    }

    private fun parseToAst(filePath: String): Node {
        val markdown = readMarkdownFile(filePath)

        val options = MutableDataSet()
        options.set(
            Parser.EXTENSIONS,
            Arrays.asList<Extension>(
                StrikethroughExtension.create(),  // 打ち消し線に対応
                TablesExtension.create(),  // テーブルに対応
                TocExtension.create() // [TOC] の部分に目次を生成する
            )
        )
        val parser = Parser.builder(options).build()

        return parser.parse(markdown)
    }


    private fun convertToSlackBlocks(document: Node): SlackBlocks {
        val slackBlockConverter = SlackBlockConverter()
        return slackBlockConverter.convert(document)
    }


    private fun readMarkdownFile(filePath: String): String {
        assertMarkdownFile(filePath)
        assertExistingFile(filePath)
        return File(filePath).readText()
    }

    private fun assertExistingFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filePath")
        }
    }

    private fun assertMarkdownFile(filePath: String) {
        if (!filePath.endsWith(".md")) {
            throw IllegalArgumentException("Not a markdown file: $filePath")
        }
    }
}