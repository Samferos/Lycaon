package iut.nantes.project.bff.dto

import java.time.LocalDate
import java.util.UUID

data class ReservationAggregatedDto(
    val id: UUID,
    val owner: SimplePersonDto,
    val peoples: List<SimplePersonDto>,
    val roomId: SimpleRoomDto,
    val start: Int,
    val end: Int,
    val day: LocalDate
)