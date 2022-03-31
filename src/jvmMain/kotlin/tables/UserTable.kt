package tables

import kotlinx.coroutines.future.await
import model.User
import requireEnv
import scanAsync
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey


@DynamoDbBean
data class UserItem(
    @get:DynamoDbPartitionKey var userId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var apartmentNumber: Int? = null,
    var admin: Boolean? = null
)

private fun User.toItem() = UserItem(
    userId = userId,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    email = email,
    apartmentNumber = apartmentNumber,
    admin = admin
)

private fun UserItem.toUser() = User(
    userId = userId!!,
    firstName = firstName!!,
    lastName = lastName!!,
    phoneNumber = phoneNumber!!,
    email = email,
    apartmentNumber = apartmentNumber!!,
    admin = admin!!
)

class UserTable {
    companion object {
        private val tableSchema = TableSchema.fromBean(UserItem::class.java)
        private val client = DynamoDbEnhancedAsyncClient.create()
        private val table = client.table(
            requireEnv("USER_TABLE"),
            tableSchema
        )
    }

    private var usersCache: MutableMap<String, User>? = null

    private suspend fun getUsersInternal() =
        usersCache ?: fetchUsers().also { usersCache = it }

    private suspend fun fetchUsers() =
        table.scanAsync().map {
            it.toUser()
        }.associateBy {
            it.userId
        }.toMutableMap()

    suspend fun putUser(user: User) {
        getUsersInternal()[user.userId] = user
        table.putItem(user.toItem()).await()
    }

    suspend fun getUser(userId: String): User? =
        getUsersInternal()[userId]
}