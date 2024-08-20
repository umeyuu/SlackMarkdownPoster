package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SlackApiClient {

    private val apiUrl = ""

    fun postToSlack(slackBlock: SlackBlocks) : String{
        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val jsonString = json.encodeToString(slackBlock)
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