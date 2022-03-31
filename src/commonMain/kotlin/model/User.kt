package model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,  // phone number for now
    val firstName: String,  // not empty
    val lastName: String,  // not empty
    val phoneNumber: String,  // without country code
    val email: String?, // optional
    val apartmentNumber: Int,  //three digits
    val admin: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (other !is User) return false
        return userId == other.userId
    }

    override fun hashCode() = userId.hashCode()
}