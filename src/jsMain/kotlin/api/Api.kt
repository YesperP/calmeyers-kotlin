package api

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import model.*

private const val BASE = "https://g9xqev2z97.execute-api.eu-west-1.amazonaws.com"
private val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

private fun HttpRequestBuilder.json(data: Any) {
    contentType(ContentType.Application.Json)
    body = data
}


interface TokenApiBase {
    suspend fun loginVibboStart(data: LoginStartRequest): LoginStartResponse
    suspend fun loginVibboEnd(data: LoginEndRequest): TokenResponse
    suspend fun loginAlt(data: LoginAltRequest): TokenResponse
}

interface ApiBase {
    suspend fun createBooking(data: CreateBookingRequest): BookingResponse
    suspend fun getBookings(): GetBookingsResponse
    suspend fun deleteBooking(bookingId: String): BookingResponse
}

object TokenApi : TokenApiBase {
    override suspend fun loginVibboStart(data: LoginStartRequest) =
        client.post<LoginStartResponse>("$BASE/token/start") { json(data) }

    override suspend fun loginVibboEnd(data: LoginEndRequest) =
        client.post<TokenResponse>("$BASE/token/end") { json(data) }

    override suspend fun loginAlt(data: LoginAltRequest) =
        client.post<TokenResponse>("$BASE/loginAlt") { json(data) }

}

class Api(private val token: String) : ApiBase {
    private fun HttpRequestBuilder.token() {
        headers { append("Authorization", "Bearer $token") }
    }

    override suspend fun createBooking(data: CreateBookingRequest) =
        client.post<BookingResponse>("$BASE/bookings") {
            token()
            json(data)
        }

    override suspend fun getBookings() =
        client.get<GetBookingsResponse>("$BASE/bookings") { token() }

    override suspend fun deleteBooking(bookingId: String) =
        client.delete<BookingResponse>("$BASE/bookings/$bookingId") { token() }

}