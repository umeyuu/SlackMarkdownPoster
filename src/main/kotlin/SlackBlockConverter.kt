package org.example

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.util.ast.Node
import org.example.extention.toHeaderBlock
import org.example.extention.toRichTextBlock

class SlackBlockConverter {

    fun convert(document: Node): SlackBlocks {
        val blocks = document.children.mapNotNull { node ->
            when (node) {
                is Heading -> node.toHeaderBlock()
                is Paragraph -> node.toRichTextBlock()
                is BulletList -> node.toRichTextBlock()
                is OrderedList -> node.toRichTextBlock()
                is FencedCodeBlock -> node.toRichTextBlock()
                else -> null
            }
        }
        return SlackBlocks(blocks = blocks)
    }
}