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



fun BulletList.toRichTextBlock(): RichTextBlock {
    val (rootList, nestedLists) = processList(this, 0, mutableListOf(), mutableListOf(), "bullet")
    nestedLists.add(rootList)
    return RichTextBlock(
        elements = mergeAndCleanRichTextLists(nestedLists)
    )
}

fun OrderedList.toRichTextBlock(): RichTextBlock {
    val (rootList, nestedLists) = processList(this, 0, mutableListOf(), mutableListOf(), "ordered")
    nestedLists.add(rootList)
    return RichTextBlock(
        elements = mergeAndCleanRichTextLists(nestedLists)
    )
}

private fun processList(
    node: Node, indent: Int,
    outputSections: MutableList<RichTextSection>,
    outputLists: MutableList<RichTextList>,
    listStyle: String
): Pair<RichTextList, MutableList<RichTextList>> {
    var child = node.firstChild
    val currentSections = mutableListOf<RichTextSection>()

    while (child != null) {
        when (child) {
            is Paragraph -> {
                val section = getRichTextSection(child, TextStyle(), mutableListOf())
                currentSections.add(section)
            }
            is BulletList, is OrderedList -> {
                val (nestedList, nestedOutput) = processList(
                    child, indent + 1, mutableListOf(), mutableListOf(),
                    if (child is BulletList) "bullet" else "ordered"
                )
                addRichTextList(outputLists, indent, currentSections, listStyle)
                outputLists.add(nestedList)
                outputLists.addAll(nestedOutput)
            }
            else -> {
                processList(child, indent, currentSections, outputLists, listStyle)
            }
        }
        child = child.next
    }

    addRichTextList(outputLists, indent, currentSections, listStyle)

    return Pair(
        RichTextList(
            style = listStyle,
            elements = outputSections.toMutableList(),
            indent = indent,
            offset = null,
            border = null
        ),
        outputLists
    )
}



private fun mergeAndCleanRichTextLists(lists: MutableList<RichTextList>): List<RichTextList> {
    val cleanedList = mutableListOf<RichTextList>()

    for (list in lists) {
        val adjustedList = list.copy(indent = list.indent)

        if (adjustedList.elements.isNotEmpty()) {
            if (cleanedList.isNotEmpty() && cleanedList.last().indent == adjustedList.indent) {
                val lastElement = cleanedList.removeAt(cleanedList.lastIndex)
                cleanedList.add(lastElement.copy(elements = lastElement.elements + adjustedList.elements))
            } else {
                cleanedList.add(adjustedList)
            }
        }
    }

    return cleanedList
}

private fun addRichTextList(
    outputLists: MutableList<RichTextList>,
    indent: Int,
    sections: MutableList<RichTextSection>,
    listStyle: String
) {
    if (sections.isNotEmpty()) {
        outputLists.add(
            RichTextList(
                style = listStyle,
                elements = sections.toMutableList(),
                indent = indent,
                offset = null,
                border = null
            )
        )
        sections.clear()
    }
}



fun Node.toSlackBlocks(): SlackBlocks {
    val blocks = this.children.mapNotNull { node ->
        when (node) {
            is Heading -> node.toHeaderBlock()
            is Paragraph -> getRichTextSection(node, TextStyle(), mutableListOf())
            is BulletList -> node.toRichTextBlock()
            is OrderedList -> node.toRichTextBlock()
            else -> null
        }
    }
    return SlackBlocks(blocks = blocks)
}
