package model

open class ResponseException(val statusCode: Int, message: String?) : Exception(message)

class BadRequestException(message: String?) : ResponseException(400, message)
class MissingAuthException(message: String?) : ResponseException(401, message)
class ForbiddenException(message: String?) : ResponseException(403, message)
