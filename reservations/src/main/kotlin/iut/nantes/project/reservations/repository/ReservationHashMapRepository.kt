package iut.nantes.project.reservations.repository

import iut.nantes.project.reservations.client.InvalidDataException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ReservationHashMapRepository : ReservationRepository {

    private val data = ConcurrentHashMap<UUID, ReservationEntity>()

    override fun save(reservation: ReservationEntity): ReservationEntity {
        val r = if (reservation.id == null) reservation.copy(id = UUID.randomUUID()) else reservation
        data[r.id] = r
        return r
    }

    override fun findById(id: UUID) = data[id]

    override fun findAll() = data.values.toList()

    override fun findByRoomAndDayBetween(roomId: Long?, dayStart: LocalDate?, dayEnd: LocalDate?): List<ReservationEntity> {
        if (dayStart != null && dayEnd != null && dayStart.isAfter(dayEnd))
            throw InvalidDataException("dayStart must be before or equal to dayEnd")

        return data.values.filter {
            (roomId == null || it.roomId == roomId) &&
                    (dayStart == null || !it.day.isBefore(dayStart)) &&
                    (dayEnd == null || !it.day.isAfter(dayEnd))
        }
    }

    override fun deleteById(id: UUID) = data.remove(id) != null
}

