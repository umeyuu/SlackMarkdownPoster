package org.example.extention

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.ext.gfm.strikethrough.Strikethrough
import com.vladsch.flexmark.util.ast.Node
import org.example.*

/**
 * H1〜H6をHeader Blockに変換する拡張関数
 */
fun Heading.toHeaderBlock() = Header(
    text = PlainText(
        text = this.text.toString()
    ),
    block_id = this.anchorRefId
)

/**
 * 普通のテキストをPlainTextに変換する拡張関数
 */
//fun Paragraph.toPlainText() = PlainText(
//    text = this.chars.toString()
//)

/**
 * styleを持つテキストであるRichTextBlockを生成する拡張関数
 */
fun Paragraph.toRichTextBlock() : RichTextBlock {
    val section = getRichTextSection(this, TextStyle(), mutableListOf())
    return RichTextBlock(
        elements = listOf(section)
    )
}

/**
 * 箇条書きを処理する拡張関数
 */
fun BulletList.toRichTextBlock(): RichTextBlock {
    val (rootList, nestedLists) = processList(this, 0, mutableListOf(), mutableListOf(), "bullet")
    nestedLists.add(rootList)
    return RichTextBlock(
        elements = mergeAndCleanRichTextLists(nestedLists)
    )
}

/**
 * 番号付きリストを処理する拡張関数
 */
fun OrderedList.toRichTextBlock(): RichTextBlock {
    val (rootList, nestedLists) = processList(this, 0, mutableListOf(), mutableListOf(), "ordered")
    nestedLists.add(rootList)
    return RichTextBlock(
        elements = mergeAndCleanRichTextLists(nestedLists)
    )
}

/**
 * コードブロックを処理する拡張関数
 */
fun FencedCodeBlock.toRichTextBlock(): RichTextBlock {
    val child = this.firstChild
    return RichTextBlock(
        elements = listOf(
            RichTextPreformatted(
                elements = listOf(
                    TextElement(
                        text = child?.chars?.toString() ?: ""
                    )
                )
            )
        )
    )
}


private fun getRichTextSection(node: Node, style: TextStyle, output: MutableList<RichTextElement>): RichTextSection {
    when (node) {
        is Text -> {
            output.add(
                TextElement(
                text = node.chars.toString(),
                style = style.copy()
            ))
            style.reset()
        }
        is Link -> {
            output.add(
                TextLink(
                text = node.text.toString(),
                url = node.url.toString(),
                style = style.copy()
            ))
            style.reset()
            return RichTextSection(elements = output)
        }
    }

    var child = node.firstChild
    while (child != null) {
        when (child) {
            is Emphasis -> style.setItalic()
            is StrongEmphasis -> style.setBold()
            is Strikethrough -> style.setStrike()
            is Code -> style.setCode()
        }
        // 再帰的に子ノードを処理
        getRichTextSection(child, style, output)
        child = child.next
    }

    return RichTextSection(elements = output)
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
            indent = indent
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
                indent = indent
            )
        )
        sections.clear()
    }
}
