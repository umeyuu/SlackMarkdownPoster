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