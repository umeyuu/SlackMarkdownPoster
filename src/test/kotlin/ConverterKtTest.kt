import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import org.example.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class ConverterKtTest{
    private val options = MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList<Extension>(
        StrikethroughExtension.create(),  // 打ち消し線に対応
        TablesExtension.create(),  // テーブルに対応
        TocExtension.create() // [TOC] の部分に目次を生成する
    ))
    private val parser: Parser = Parser.builder(options).build()


    @Test
    fun `Paragraphの処理`(){
        // given
        val markdown = """
            |Hello there, ~~_**I am a basic rich text block!**_~~ and I am not!
            |""".trimMargin()
        val document: Node = parser.parse(markdown)
        val exception = RichTextSection(
            elements = listOf(
                RichTextElement(
                    type = "text",
                    text = "Hello there, ",
                    style = TextStyle()
                ),
                RichTextElement(
                    type = "text",
                    text = "I am a basic rich text block!",
                    style = TextStyle(bold = true, italic = true, strike = true)
                ),
                RichTextElement(
                    type = "text",
                    text = " and I am not!",
                    style = TextStyle()
                )
            )
        )
        // when
        val actual = getRichTextSection(document, TextStyle(), mutableListOf())
        // then
        assertEquals(exception, actual)
    }
}