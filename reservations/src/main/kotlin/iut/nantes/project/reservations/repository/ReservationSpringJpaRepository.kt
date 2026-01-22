package iut.nantes.project.reservations.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

interface ReservationSpringJpaRepository : JpaRepository<ReservationEntity, UUID>

@Repository
class ReservationJpaRepository(
    private val jpa: ReservationSpringJpaRepository
) : ReservationRepository {

    override fun save(reservation: ReservationEntity): ReservationEntity = jpa.save(reservation)

    override fun findById(id: UUID): ReservationEntity? = jpa.findById(id).orElse(null)

    override fun findAll(): List<ReservationEntity> = jpa.findAll()

    override fun deleteById(id: UUID): Boolean {
        if (!jpa.existsById(id)) return false
        jpa.deleteById(id)
        return true
    }

    override fun findByRoomAndDayBetween(
        ownerId: Long?, roomId: Long?, dayStart: LocalDate?, dayEnd: LocalDate?
    ): List<ReservationEntity> {

        return jpa.findAll().filter {
            (ownerId == null || it.ownerId == ownerId) && (roomId == null || it.roomId == roomId) && (dayStart == null || !it.day.isBefore(
                dayStart
            )) && (dayEnd == null || !it.day.isAfter(dayEnd))
        }
    }
}
