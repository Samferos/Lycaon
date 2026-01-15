package iut.nantes.project.peoples.error


import java.time.ZonedDateTime

data class ApiErrorResponse(
    val timestamp: String = ZonedDateTime.now().toString(),
    val status: Int,
    val message: String
)