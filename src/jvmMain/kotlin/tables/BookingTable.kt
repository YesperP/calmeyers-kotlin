package tables

import dynamoDbTtl
import kotlinx.coroutines.future.await
import kotlinx.datetime.Instant
import model.BookUser
import model.Booking
import model.User
import requireEnv
import scanAsync
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import kotlin.time.Duration

@DynamoDbBean
data class BookUserItem(
    var userId: String? = null,
    var time: String? = null
)

@DynamoDbBean
data class BookingItem(
    @get:DynamoDbPartitionKey var bookingId: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var creator: BookUserItem? = null,
    var deleter: BookUserItem? = null,
    var ttl: Long? = null
)

private fun BookUser.toItem() = BookUserItem(
    userId = user.userId,
    time = time.toString()
)

private suspend fun BookUserItem.toObj(userTable: UserTable) = BookUser(
    user = userTable.getUser(userId!!) ?: run {
        println("ERROR: Could not find user with id: $userId")
        User(
            userId = userId!!,
            firstName = "<Unknown>",
            lastName = "<Unknown>",
            phoneNumber = userId!!,
            email = null,
            apartmentNumber = 0,
            admin = false
        )
    },
    time = Instant.parse(time!!)
)

private fun Booking.toItem(ttl: Duration) = BookingItem(
    bookingId = bookingId,
    startTime = startTime.toString(),
    endTime = endTime.toString(),
    creator = creator.toItem(),
    deleter = deleter?.toItem(),
    ttl = dynamoDbTtl(ttl)
)

private suspend fun BookingItem.toObj(userTable: UserTable) = Booking(
    bookingId = bookingId!!,
    startTime = Instant.parse(startTime!!),
    endTime = Instant.parse(endTime!!),
    creator = creator!!.toObj(userTable),
    deleter = deleter?.toObj(userTable)
)

class BookingTable(private val userTable: UserTable) {
    companion object {
        private val tableSchema = TableSchema.fromBean(BookingItem::class.java)
        private val ttl = Duration.days(365)
    }

    private val client = DynamoDbEnhancedAsyncClient.create()
    private val table = client.table(
        requireEnv("BOOKING_TABLE"),
        tableSchema
    )

    suspend fun getBookings() =
        table.scanAsync().map { it.toObj(userTable) }

    suspend fun setBooking(booking: Booking) {
        table.putItem(booking.toItem(ttl)).await()
    }

    suspend fun getBooking(bookingId: String) =
        table.getItem(BookingItem(bookingId = bookingId)).await()?.toObj(userTable)
}