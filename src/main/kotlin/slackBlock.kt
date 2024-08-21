package org.example

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// シールドクラス

sealed class SlackBlock {
    abstract fun toJson(): JsonObject
}

sealed class RichTextBlockElement {
    abstract fun toJson(): JsonObject
}

sealed class RichTextElement {
    abstract fun toJson(): JsonObject
}


data class SlackBlocks(
    val blocks: List<SlackBlock>
) {
    fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "blocks" to JsonArray(blocks.map { it.toJson() })
        ))
    }
}


data class PlainText(
    val type: String = "plain_text",
    val text: String,
    val emoji: Boolean = false,
) : SlackBlock() {
    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "type" to JsonPrimitive(type),
            "text" to JsonPrimitive(text),
            "emoji" to JsonPrimitive(emoji)
        ))
    }
}


data class Header(
    val text: PlainText,
    val block_id: String?
): SlackBlock() {
    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "type" to JsonPrimitive("header"),
            "text" to text.toJson(),
            "block_id" to JsonPrimitive(block_id)
        ))
    }
}


data class RichTextBlock(
    val elements: List<RichTextBlockElement>
): SlackBlock() {
    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "type" to JsonPrimitive("rich_text"),
            "elements" to JsonArray(elements.map { it.toJson() })
        ))
    }

}


data class RichTextSection(
    val elements: List<RichTextElement>
): RichTextBlockElement() {
    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "type" to JsonPrimitive("rich_text_section"),
            "elements" to JsonArray(elements.map { it.toJson() })
        ))
    }

}


data class RichTextList(
    val style: String, // "ordered" or "bullet"
    val elements: List<RichTextSection>,
    val indent: Int?, // インデントするピクセル数
    val offset: Int? = 0, // オフセットするピクセル数
    val border: Int? = 0 // 境界線の太さ
) : RichTextBlockElement() {
    override fun toJson(): JsonObject {
        return JsonObject(
            mapOf(
                "type" to JsonPrimitive("rich_text_list"),
                "style" to JsonPrimitive(style),
                "elements" to JsonArray(elements.map { it.toJson() }),
                "indent" to JsonPrimitive(indent),
                "offset" to JsonPrimitive(offset),
                "border" to JsonPrimitive(border)
            )
        )
    }
}



data class RichTextPreformatted(
    val elements: List<RichTextElement>,
    val border: Int? = 0
) : RichTextBlockElement() {
    override fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "type" to JsonPrimitive("rich_text_preformatted"),
            "elements" to JsonArray(elements.map { it.toJson() }),
            "border" to JsonPrimitive(border)
        ))
    }

}



data class TextElement(
    val text: String,
    val style: TextStyle? = null
) : RichTextElement() {
    override fun toJson(): JsonObject {
        val jsonMap = mutableMapOf<String, JsonElement>(
            "type" to JsonPrimitive("text"),
            "text" to JsonPrimitive(text)
        )

        style?.let {
            jsonMap["style"] = it.toJson()
        }

        return JsonObject(jsonMap)
    }
}



data class TextLink(
    val url: String,
    val text: String,
    val unsafe: Boolean? = null,
    val style: TextStyle? = null
) : RichTextElement() {
    override fun toJson(): JsonObject {
        val jsonMap = mutableMapOf<String, JsonElement>(
            "type" to JsonPrimitive("link"),
            "url" to JsonPrimitive(url),
            "text" to JsonPrimitive(text)
        )

        unsafe?.let {
            jsonMap["url"] = JsonPrimitive(url)
        }

        style?.let {
            jsonMap["style"] = it.toJson()
        }

        return JsonObject(jsonMap)
    }
}


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

    fun toJson(): JsonObject {
        return JsonObject(mapOf(
            "bold" to JsonPrimitive(bold),
            "italic" to JsonPrimitive(italic),
            "strike" to JsonPrimitive(strike),
            "code" to JsonPrimitive(code)
        ))
    }
}
