package iut.nantes.project.reservations.repository

import java.time.LocalDate
import java.util.UUID

interface ReservationRepository {
    fun save(reservation: ReservationEntity): ReservationEntity
    fun findById(id: UUID): ReservationEntity?
    fun findAll(): List<ReservationEntity>
    fun findByRoomAndDayBetween(roomId: Long?, dayStart: LocalDate?, dayEnd: LocalDate?): List<ReservationEntity>
    fun deleteById(id: UUID): Boolean
}
