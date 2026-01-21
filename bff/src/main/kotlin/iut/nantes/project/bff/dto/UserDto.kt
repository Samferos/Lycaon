package iut.nantes.project.bff.dto

data class UserDto(
    val login: String,
    val password: String,
    val isAdmin: Boolean
)