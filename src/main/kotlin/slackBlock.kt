package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// シールドクラス
@Serializable
sealed class SlackBlock

@Serializable
sealed class RichTextBlockElement

@Serializable
sealed class RichTextElement

@Serializable
data class SlackBlocks(
    val blocks: List<SlackBlock>
)

@Serializable
data class PlainText(
    @SerialName("type") val type: String = "plain_text",
    val text: String,
    val emoji: Boolean = false,
) : SlackBlock()

@Serializable
@SerialName("header")
data class Header(
    val text: PlainText,
    val block_id: String? = null
): SlackBlock()

@Serializable
@SerialName("rich_text")
data class RichTextBlock(
    val elements: List<RichTextBlockElement>
): SlackBlock()

@Serializable
@SerialName("rich_text_section")
data class RichTextSection(
    val elements: List<RichTextElement>
): RichTextBlockElement()

@Serializable
@SerialName("rich_text_list")
data class RichTextList(
    val style: String, // "ordered" or "bullet"
    val elements: List<RichTextSection>,
    val indent: Int?, // インデントするピクセル数
    val offset: Int?, // オフセットするピクセル数
    val border: Int? // 境界線の太さ
) : RichTextBlockElement()

@Serializable
@SerialName("rich_text_preformatted")
data class RichTextPreformatted(
    val elements: List<RichTextElement>,
    val border: Int? = null
) : RichTextBlockElement()

@Serializable
@SerialName("text")
data class TextElement(
    val text: String,
    val style: TextStyle? = null
): RichTextElement()

@Serializable
@SerialName("link")
data class TextLink(
    val url: String,
    val text: String,
    val unsafe: Boolean? = null,
    val style: TextStyle? = null
) : RichTextElement()

@Serializable
data class TextStyle(
    var bold: Boolean = false,
    var italic: Boolean = false,
    var strike: Boolean = false,
    var code: Boolean = false
) {
    fun setItalic() {
        italic = true
    }

    fun setBold() {
        bold = true
    }

    fun setStrike() {
        strike = true
    }

    fun setCode() {
        code = true
    }

    fun reset() {
        bold = false
        italic = false
        strike = false
        code = false
    }
}
