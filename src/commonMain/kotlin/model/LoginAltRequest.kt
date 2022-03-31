package model

import kotlinx.serialization.Serializable

@Serializable
data class LoginAltRequest(
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val apartmentNumber: String
)