package org.example

import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough
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

fun Node.toRichTextSection() :RichTextSection {
    var output = mutableListOf<RichTextElement>()
    for (child in this.children) {
        output.add(RichTextElement(
            type = "text",
            text = child.chars.toString(),
            style = TextStyle(
                bold = child::class.simpleName == "StrongEmphasis()",
                italic = child::class == Emphasis(),
                strike = child::class == Strikethrough()
            )
        ))
    }
    return RichTextSection(elements = output)
}



fun Node.toSlackBlocks(): SlackBlocks {
    val blocks = this.children.mapNotNull { node ->
        when (node) {
            is Heading -> node.toHeaderBlock()
            is Paragraph -> node.toRichTextSection()
            else -> null
        }
    }

    return SlackBlocks(blocks = blocks)
}
