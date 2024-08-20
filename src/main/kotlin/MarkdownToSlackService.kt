package org.example

import java.io.File

class MarkdownToSlackService {


    private fun readMarkdownFile(filePath: String): String {
        return File(filePath).readText()
    }


}