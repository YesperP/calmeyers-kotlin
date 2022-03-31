package handlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import model.BadRequestException
import model.ResponseException
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class ApiHeaders(
    val authorization: String? = null
)

@Serializable
data class ApiRequest(
    val headers: ApiHeaders,
    val body: String? = null,
    val pathParameters: Map<String, String>? = null
)

@Serializable
private data class ApiResponse(
    val statusCode: Int,
    val headers: JsonObject,
    val body: String
)

abstract class AwsApiHandler : RequestStreamHandler {
    companion object {
        private val jsonHeaders = buildJsonObject {
            put("Content-Type", ContentType.Application.Json.toString())
            put("Access-Control-Allow-Origin", "*")
            put("Access-Control-Allow-Credentials", true)
        }
        private val textHeaders = buildJsonObject {
            put("Content-Type", ContentType.Text.Plain.toString())
            put("Access-Control-Allow-Origin", "*")
            put("Access-Control-Allow-Credentials", true)
        }
    }

    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> decodeBody(input: ApiRequest) = try {
        input.body?.let { json.decodeFromString<T>(it) } ?: throw BadRequestException("Missing body")
    } catch (e: Exception) {
        throw BadRequestException(e.message)
    }

    inline fun <reified T> encodeBody(obj: T) = json.encodeToString(obj)

    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) = runBlocking {
        val reader = input.reader()
        val writer = output.writer()
        try {
            val response = try {
                val eventInput = reader.readText()
                println("Event: $eventInput")
                val request = json.decodeFromString<ApiRequest>(eventInput)
                val body = handleRequest(request, context)
                println("Response body: $body")
                ApiResponse(200, jsonHeaders, body)
            } catch (e: ResponseException) {
                e.printStackTrace()
                ApiResponse(e.statusCode, textHeaders, e.message ?: "Unknown error")
            } catch (e: Exception) {
                e.printStackTrace()
                ApiResponse(500, textHeaders, e.message ?: "Unknown error")
            }
            writer.write(json.encodeToString(response))
        } finally {
            reader.close()
            writer.close()
        }
    }

    abstract suspend fun handleRequest(input: ApiRequest, context: Context): String
}
