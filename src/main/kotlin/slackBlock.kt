package org.example


sealed class SlackBlock // Slack Blockの要素を表すシールドクラス
sealed class RichTextBlockElement // Rich Text Blockの要素を表すシールドクラス
sealed class RichTextElement // Rich Textの要素を表すシールドクラス


data class SlackBlocks(
    val blocks: List<SlackBlock>
)

data class PlainText(
    val type: String,
    val text: String,
    val emoji: Boolean = false,
    val verbatim: Boolean = false
): SlackBlock()


data class Header(
    val type: String = "header",
    val text: PlainText,
    val block_id: String?
): SlackBlock()


// SlackのRich Text Block全体を表すデータクラス
data class RichTextBlock(
    val type: String = "rich_text",
    val elements: List<RichTextBlockElement> // 要素のリスト
): SlackBlock()

// Rich Textのセクションを表すデータクラス
data class RichTextSection(
    val type: String = "rich_text_section",
    val elements: List<RichTextElement> // 要素のリスト
): RichTextBlockElement()


data class RichTextList(
    val type: String = "rich_text_list",
    val style: String, // "ordered" or "bullet"
    val elements: List<RichTextSection>,
    val indent: Int?, // インデントするピクセル数
    val offset: Int?, // オフセットするピクセル数
    val border: Int?, // 境界線の太さ
): RichTextBlockElement()

data class RichTextPreformatted(
    val type: String = "rich_text_preformatted",
    val elements: List<RichTextElement>,
    val border: Int? = null
): RichTextBlockElement()



// テキスト要素を表すデータクラス
data class TextElement(
    val type: String = "text",
    val text: String,
    val style: TextStyle? = null // Optional: スタイル情報
): RichTextElement()

data class TextLink(
    val type: String = "link",
    val url: String,
    val text: String,
    val unsafe: Boolean? = null,
    val style: TextStyle? = null
): RichTextElement()

// テキストのスタイルを表すデータクラス
data class TextStyle(
    var bold: Boolean = false,
    var italic: Boolean = false,
    var strike: Boolean = false,
    var code: Boolean = false,
){
    fun setItalic(){
        italic = true
    }
    fun setBold(){
        bold = true
    }
    fun setStrike(){
        strike = true
    }
    fun setCode(){
        code = true
    }

    fun reset(){
        bold = false
        italic = false
        strike = false
        code = false
    }
}
