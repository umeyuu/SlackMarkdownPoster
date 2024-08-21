package org.example

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class SlackApiClient {

    private val apiUrl = "https://hooks.slack.com/services/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"

    fun postToSlack(slackBlock: SlackBlocks) : String{
        val jsonObject = slackBlock.toJson()
        val jsonString = jsonObject.toString()
        return sendHttpsPostRequest(jsonString)

    }

    fun sendHttpsPostRequest(payload: String): String {
        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }

        connection.outputStream.use { outputStream ->
            writePayload(outputStream, payload)
        }

        val responseCode = connection.responseCode
        val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }

        connection.disconnect()

        return "Response Code: $responseCode\nResponse Message: $responseMessage"
    }

    private fun writePayload(outputStream: OutputStream, payload: String) {
        outputStream.write(payload.toByteArray(Charsets.UTF_8))
        outputStream.flush()
    }

}