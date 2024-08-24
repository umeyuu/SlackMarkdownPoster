package org.example

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.util.ast.Node
import org.example.extention.toHeaderBlock
import org.example.extention.toRichTextBlock

class SlackBlockConverter {

    fun convert(document: Node): SlackBlocks {
        val blocks = document.toSlackBlocks()
        val formattedBlocks = blocks.applyFormatting()
        return SlackBlocks(
            attachments = listOf(
                SlackBlocksAttachment(
                    color = ConfigLoader.getProperty("slack.color") ?: "#36a64f",
                    blocks = formattedBlocks
                )
            )
        )
    }

    private fun Node.toSlackBlocks(): List<SlackBlock> =
        this.children.mapNotNull { node ->
            when (node) {
                is Heading -> node.toHeaderBlock()
                is Paragraph -> node.toRichTextBlock()
                is BulletList -> node.toRichTextBlock()
                is OrderedList -> node.toRichTextBlock()
                is FencedCodeBlock -> node.toRichTextBlock()
                else -> null
            }
        }

    private fun List<SlackBlock>.applyFormatting(): List<SlackBlock> {
        val sectionTitles = ConfigLoader.getSectionTitles()
        val contentTitle = ConfigLoader.getProperty("content.title")
        val excludeList = ConfigLoader.getExcludeList()

        return this.takeWhile { block ->
            !(block is Header && excludeList.contains(block.text.text))
        }.flatMap { block ->
            when {
                block is Header && block.text.text.contains(contentTitle.toString()) -> listOf(block)
                block is Header && sectionTitles.any { block.text.text.contains(it) } -> block.toTextImageContextWithDivider()
                block is Header -> listOf(block.toTextImageContext())
                else -> listOf(block)
            }
        }
    }

    private fun Header.toTextImageContextWithDivider(): List<SlackBlock> {
        return listOf(
            Divider(""),
            this.toTextImageContext(),
            Divider("")
        )
    }

    private fun Header.toTextImageContext(): TextImageContext {
        val sectionKey = ConfigLoader.getSectionKeys().firstOrNull {
            this.text.text.contains(ConfigLoader.getProperty("content.$it.title") ?: "")
        }

        val elements = mutableListOf<ContextElement>()
        sectionKey?.let {
            elements.add(ImageContextElement(
                image_url = ConfigLoader.getProperty("content.$it.image_url") ?: "",
                alt_text = ConfigLoader.getProperty("content.$it.alt_text") ?: ""
            ))
        }
        elements.add(MarkdownContextElement(
            text = "*${this.text.text}*"
        ))

        return TextImageContext(elements = elements)
    }
}
