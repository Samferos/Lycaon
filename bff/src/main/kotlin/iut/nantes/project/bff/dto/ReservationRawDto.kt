package iut.nantes.project.bff.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.UUID

data class ReservationRawDto(
    val id: UUID,
    @JsonProperty("ownerId") val ownerId: Long,
    val peoples: List<Long>,
    val roomId: Long,
    val start: Int,
    val end: Int,
    val day: LocalDate
)