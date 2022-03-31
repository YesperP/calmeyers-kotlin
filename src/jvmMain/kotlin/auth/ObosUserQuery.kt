import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import model.ForbiddenException
import model.User

@Serializable
private data class QueryResult(val data: QueryData)

@Serializable
private data class QueryData(val organization: Organization? = null)

@Serializable
private data class Organization(val obosCompanyNumber: String, val viewerAsMember: Member)

@Serializable
private data class Member(
    val personId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val roles: List<String>,
    val residencies: List<Residence>
)

@Serializable
private data class Residence(val type: String, val apartment: Apartment)

@Serializable
private data class Apartment(val apartmentNumber: Int)

private val query = """
    query vibboMyApartmentPage(${'$'}organizationSlug: OrganizationID!) {
        organization(id: ${'$'}organizationSlug) {
            obosCompanyNumber       
            viewerAsMember { 
                personId 
                firstName
                lastName
                phoneNumber
                email
                roles 
                residencies {       
                    type
                    apartment {
                        apartmentNumber                                            
                    }              
                }         
            }    
        }
    }
""".trimIndent()

suspend fun getUserInfo(client: HttpClient): User {
    val queryResult: QueryResult = client.get("https://vibbo.no/graphql?name=vibboMyApartmentPage") {
        json {
            put("operationName", "vibboMyApartmentPage")
            put("query", query)
            putJsonObject("variables") {
                put("organizationSlug", "calmeyersgt-4")
            }
            put("organizationSlug", "calmeyersgt-4")
        }
    }
    println("Obos user query result: $queryResult")

    if (queryResult.data.organization == null)
        throw ForbiddenException("You are not registered at Calmeyers Gate 4 in vibbo.no")

    with(queryResult.data.organization.viewerAsMember) {
        return User(
            userId = phoneNumber,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            email = email,
            apartmentNumber = residencies[0].apartment.apartmentNumber,
            admin = roles.any { "BOARD_MEMBER" in it }
        )
    }
}