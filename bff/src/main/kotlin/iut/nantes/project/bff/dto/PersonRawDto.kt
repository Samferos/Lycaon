package iut.nantes.project.bff.dto

data class PersonRawDto(
    val id: Long, val firstName: String, val lastName: String, val age: Int, val address: AddressDto
)