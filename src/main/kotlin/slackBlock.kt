package org.example


sealed class Block

data class SlackBlocks(
    val blocks: List<Block>
)

data class PlainText(
    val type: String,
    val text: String,
    val emoji: Boolean = false,
    val verbatim: Boolean = false
): Block()


data class Header(
    val type: String = "header",
    val text: PlainText,
    val block_id: String?
): Block()


// SlackのRich Text Block全体を表すデータクラス
data class RichTextBlock(
    val type: String = "rich_text",
    val elements: List<RichTextElement> // 要素のリスト
)

// Rich Textのセクションを表すデータクラス
data class RichTextSection(
    val type: String = "rich_text_section",
    val elements: List<RichTextElement> // 要素のリスト
): Block()

// テキスト要素を表すデータクラス
data class RichTextElement(
    val type: String,  // "text"
    val text: String,
    val style: TextStyle? = null // Optional: スタイル情報
)

// テキストのスタイルを表すデータクラス
data class TextStyle(
    var bold: Boolean = false,
    var italic: Boolean = false,
    var strike: Boolean = false,
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

    fun reset(){
        bold = false
        italic = false
        strike = false
    }
}
