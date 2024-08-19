package org.example

import com.ibm.icu.impl.TextTrieMap.Output
import com.vladsch.flexmark.ast.*
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


fun getRichTextSection(node: Node, style: TextStyle, output: MutableList<RichTextElement>) : RichTextSection {
    if (node is Text) {
        output.add(RichTextElement(
            type = "text",
            text = node.chars.toString(),
            style = style.copy()
        ))
        style.reset()
    }
    var child = node.firstChild
    while (child != null) {
        when(child) {
            is Emphasis -> style.setItalic()
            is StrongEmphasis -> style.setBold()
            is Strikethrough -> style.setStrike()
        }
        getRichTextSection(child, style, output)
        child = child.next
    }
    return RichTextSection(elements = output)
}


fun Node.toSlackBlocks(): SlackBlocks {
    val blocks = this.children.mapNotNull { node ->
        when (node) {
            is Heading -> node.toHeaderBlock()
            is Paragraph -> getRichTextSection(node, TextStyle(), mutableListOf())
            else -> null
        }
    }

    return SlackBlocks(blocks = blocks)
}
