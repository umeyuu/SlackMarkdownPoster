package org.example

import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.util.ast.Node

fun Heading.toHeaderBlock() = Header(
    text = PlainText(
        type = "plain_text",
        text = this.text.toString()
    ),
    block_id = this.anchorRefId
)

fun Paragraph.toPlainText() = PlainText(
    type = "plain_text",
    text = this.chars.toString()
)

fun Node.toSlackBlocks(): SlackBlocks {
    val blocks = this.children.mapNotNull { node ->
        when (node) {
            is Heading -> node.toHeaderBlock()
            is Paragraph -> node.toPlainText()
            else -> null
        }
    }

    return SlackBlocks(blocks = blocks)
}
