import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.future.await
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import kotlin.time.Duration


/**
 * Note that this requires install(JsonFeature) on HttpClient
 */
fun HttpRequestBuilder.json(builderAction: JsonObjectBuilder.() -> Unit) {
    contentType(ContentType.Application.Json)
    body = buildJsonObject(builderAction)
}

suspend fun <T> DynamoDbAsyncTable<T>.scanAsync(consistentRead: Boolean = false): List<T> {
    val items = mutableListOf<T>()
    scan(
        ScanEnhancedRequest.builder().consistentRead(consistentRead).build()
    ).subscribe {
        items.addAll(it.items())
    }.await()
    return items
}

fun dynamoDbTtl(duration: Duration) = (Clock.System.now() + duration).epochSeconds

fun requireEnv(key: String) = System.getenv(key) ?: throw Exception("Key $key not in env.")