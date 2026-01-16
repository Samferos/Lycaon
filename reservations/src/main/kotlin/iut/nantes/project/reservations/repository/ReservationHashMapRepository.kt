package iut.nantes.project.reservations.repository

import java.time.LocalDate
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ReservationHashMapRepository : ReservationRepository {

    private val data = ConcurrentHashMap<UUID, ReservationEntity>()

    override fun save(reservation: ReservationEntity): ReservationEntity {
        val id = reservation.id
        val r = reservation.copy(id = id)
        data[id] = r
        return r
    }

    override fun findById(id: UUID): ReservationEntity? =
        data[id]

    override fun findAll(): List<ReservationEntity> =
        data.values.toList()

    override fun deleteById(id: UUID): Boolean =
        data.remove(id) != null

    override fun findByRoomAndDayBetween(
        roomId: Long?,
        dayStart: LocalDate?,
        dayEnd: LocalDate?
    ): List<ReservationEntity> {
        return data.values.filter { reservation ->
            (roomId == null || reservation.roomId == roomId) &&
                    (dayStart == null || !reservation.day.isBefore(dayStart)) &&
                    (dayEnd == null || !reservation.day.isAfter(dayEnd))
        }
    }
}


