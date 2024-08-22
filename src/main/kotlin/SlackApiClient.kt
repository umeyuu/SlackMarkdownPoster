package org.example

import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class SlackApiClient {

    private val webhookUrl = System.getenv("SLACK_WEBHOOK_URL")

    fun postToSlack(slackBlock: SlackBlocks) : String{
        val jsonObject = slackBlock.toJson()
        val jsonString = jsonObject.toString()
        return sendHttpsPostRequest(jsonString)
    }

    private fun sendHttpsPostRequest(payload: String): String {
        val connection = (URL(webhookUrl).openConnection() as HttpURLConnection).apply {
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