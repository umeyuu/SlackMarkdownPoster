import com.vladsch.flexmark.ast.BulletList
import com.vladsch.flexmark.ast.OrderedList
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
                TextElement(
                    text = "Hello there, ",
                    style = TextStyle()
                ),
                TextElement(
                    text = "I am a basic rich text block!",
                    style = TextStyle(bold = true, italic = true, strike = true)
                ),
                TextElement(
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

    @Test
    fun `Linkの処理`(){
        // given
        val markdown = """
            |GAFA
            |
            |[Google](https://www.google.com)
            |
            |[Amazon](https://www.amazon.com)
            |""".trimMargin()
        val document: Node = parser.parse(markdown)
        val exception = RichTextSection(
            elements = listOf(
                TextElement(
                    text = "GAFA",
                    style = TextStyle()
                ),
                TextLink(
                    text = "Google",
                    url = "https://www.google.com",
                    style = TextStyle()
                ),
                TextLink(
                    text = "Amazon",
                    url = "https://www.amazon.com",
                    style = TextStyle()
                )
            )
        )
        // when
        val actual = getRichTextSection(document, TextStyle(), mutableListOf())
        // then
        assertEquals(exception, actual)
    }

    @Test
    fun `1階層のBulletListの処理`(){
        // given
        val markdown = """
            |- item1
            |- item2
            |""".trimMargin()
        val document: Node = parser.parse(markdown)
        val child: BulletList = if (document.firstChild is BulletList) document.firstChild as BulletList else document.firstChild?.next as BulletList
        val exception = listOf(
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1",
                                style = TextStyle()
                            )
                        )
                    ),
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item2",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 0,
                offset = null,
                border = null
            )
        )
        // when
        val actual = child.toRichTextBlock()
        // then
        assertEquals(RichTextBlock(elements = exception), actual)
    }

    @Test
    fun `2階層のBulletListの処理`(){
        // given
        val markdown = """
            |- item1
            |  - item1-1
            |  - item1-2
            |    - item1-2-1
            |- item2
            |  - item2-1
            |""".trimMargin()

        val document: Node = parser.parse(markdown)
        val child: BulletList = if (document.firstChild is BulletList) document.firstChild as BulletList else document.firstChild?.next as BulletList
        val exception = listOf(
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 0,
                offset = null,
                border = null
            ),RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1-1",
                                style = TextStyle()
                            )
                        )
                    ),
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1-2",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 1,
                offset = null,
                border = null
            ),
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1-2-1",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 2,
                offset = null,
                border = null
            ),
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item2",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 0,
                offset = null,
                border = null
            ),
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item2-1",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 1,
                offset = null,
                border = null
            )
        )

        // when
        val actual = child.toRichTextBlock()
        // then
        assertEquals(RichTextBlock(elements = exception), actual)
    }

    @Test
    fun `1階層のOrderdList`() {
        // given
        val markdown = """
            |1. item1
            |2. item2
            |""".trimMargin()
        val document: Node = parser.parse(markdown)
        val child: OrderedList =
            if (document.firstChild is OrderedList) document.firstChild as OrderedList else document.firstChild?.next as OrderedList
        val exception = listOf(
            RichTextList(
                style = "ordered",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1",
                                style = TextStyle()
                            )
                        )
                    ),
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item2",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 0,
                offset = null,
                border = null
            )
        )
        // when
        val actual = child.toRichTextBlock()
        // then
        assertEquals(RichTextBlock(elements = exception), actual)
    }

    @Test
    fun `BulletListとOrderedListの混在`() {
        // given
        val markdown = """
            |- item1
            |  1. item2
            |     - item3
            |""".trimMargin()
        val document: Node = parser.parse(markdown)
        val child =
            if (document.firstChild is BulletList) document.firstChild as BulletList else document.firstChild?.next as BulletList
        val exception = listOf(
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item1",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 0,
                offset = null,
                border = null
            ),
            RichTextList(
                style = "ordered",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item2",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 1,
                offset = null,
                border = null
            ),
            RichTextList(
                style = "bullet",
                elements = listOf(
                    RichTextSection(
                        elements = listOf(
                            TextElement(
                                text = "item3",
                                style = TextStyle()
                            )
                        )
                    )
                ),
                indent = 2,
                offset = null,
                border = null
            )
        )
        // when
        val actual = child.toRichTextBlock()
        // then
        assertEquals(RichTextBlock(elements = exception), actual)
    }
}