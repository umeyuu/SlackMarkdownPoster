package org.example

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import java.io.File
import java.util.*

fun main() {
    val options = MutableDataSet()
    options.set(
        Parser.EXTENSIONS,
        Arrays.asList<Extension>(
            StrikethroughExtension.create(),  // 打ち消し線に対応
            TablesExtension.create(),  // テーブルに対応
            TocExtension.create() // [TOC] の部分に目次を生成する
        )
    )

    val markdown = readMarkdownFile("sample-file/sample.md")

    val parser: Parser = Parser.builder(options).build()
    val document: Node = parser.parse(markdown)

    // ASTをSlackのブロックに変換
    val slackBlocks = document.toSlackBlocks()

    // 結果を改行区切りで出力
    slackBlocks.blocks.forEach { block ->
        println(block)
    }
}


// Markdownファイルを読み込んでテキストを返す関数
fun readMarkdownFile(filePath: String): String {
    return File(filePath).readText()
}