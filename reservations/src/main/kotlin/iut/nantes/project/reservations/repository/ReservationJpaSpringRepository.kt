package iut.nantes.project.reservations.repository

import iut.nantes.project.reservations.client.InvalidDataException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface ReservationJpaSpringRepository : JpaRepository<ReservationEntity, UUID> {
    fun findByRoomIdAndDayBetween(roomId: Long, dayStart: LocalDate, dayEnd: LocalDate): List<ReservationEntity>
    fun findByDayBetween(dayStart: LocalDate, dayEnd: LocalDate): List<ReservationEntity>
}

class ReservationJpaRepository(
    private val springRepo: ReservationJpaSpringRepository
) : ReservationRepository {

    override fun save(reservation: ReservationEntity): ReservationEntity = springRepo.save(reservation)

    override fun findById(id: UUID): ReservationEntity? = springRepo.findById(id).orElse(null)

    override fun findAll(): List<ReservationEntity> = springRepo.findAll()

    override fun findByRoomAndDayBetween(roomId: Long?, dayStart: LocalDate?, dayEnd: LocalDate?): List<ReservationEntity> {
        if (dayStart != null && dayEnd != null && dayStart.isAfter(dayEnd))
            throw InvalidDataException("dayStart must be before or equal to dayEnd")

        return when {
            roomId != null && dayStart != null && dayEnd != null -> springRepo.findByRoomIdAndDayBetween(roomId, dayStart, dayEnd)
            dayStart != null && dayEnd != null -> springRepo.findByDayBetween(dayStart, dayEnd)
            roomId != null -> springRepo.findAll().filter { it.roomId == roomId }
            else -> springRepo.findAll()
        }
    }

    override fun deleteById(id: UUID): Boolean {
        if (!springRepo.existsById(id)) return false
        springRepo.deleteById(id)
        return true
    }
}
