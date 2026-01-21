package iut.nantes.project.bff.dto

import java.util.UUID

data class PersonAggregatedDto(
    val firstName: String, val lastName: String, val age: Int, val address: AddressDto, val reservations: List<UUID>
)