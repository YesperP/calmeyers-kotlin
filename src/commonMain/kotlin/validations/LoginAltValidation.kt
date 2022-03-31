package validations

import model.LoginAltRequest

object LoginAltValidation {
    /** Would have been nice with regex but there is a bug in JS **/

    //private val phoneRegex = Regex("""^+47[0-9]{8}$""")
    //private val aptRegex = """^[2-8]0[1-9]$""".toRegex()

    private val apartmentNumbers = listOf(
        201..208,
        301..308,
        401..408,
        501..508,
        601..608,
        701..705,
        801..804
    ).flatten()

    private fun validPhone(s: String) {
        if (s.length != 8) throw Exception("Invalid phone number: Should be 8 digits.")
        if (s.toIntOrNull() == null) throw Exception("Invalid phone number: Not a number")
    }

    private fun validApartmentNumber(s: String) {
        if (s.length != 3) throw Exception("Invalid apartment number: Length")
        if (s.toIntOrNull() == null) throw Exception("Invalid apartment number: Not a number")
        if (s.toInt() !in apartmentNumbers) throw Exception("Invalid apartment number: Not an apartment in Calmeyers")
    }

    fun validate(request: LoginAltRequest) {
        validPhone(request.phoneNumber)
        if (request.firstName.isEmpty())
            throw Exception("Invalid first name")
        if (request.lastName.isEmpty())
            throw Exception("Invalid last name")
        validApartmentNumber(request.apartmentNumber)
    }
}