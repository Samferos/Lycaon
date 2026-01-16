package iut.nantes.project.reservations.service

import iut.nantes.project.reservations.client.ReservationConflictException
import iut.nantes.project.reservations.client.InvalidDataException
import iut.nantes.project.reservations.client.NotFoundException
import iut.nantes.project.reservations.client.WebClientService
import iut.nantes.project.reservations.controller.ReservationDto
import iut.nantes.project.reservations.repository.ReservationRepository
import iut.nantes.project.reservations.repository.ReservationEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class ReservationService(
    private val repository: ReservationRepository,
    private val webClientService: WebClientService
) {

    fun createReservation(dto: ReservationDto): ReservationEntity {

        val peoplesExist = webClientService.checkAllPeoplesExist(dto.peoples).block() ?: false
        if (!peoplesExist) throw InvalidDataException("Une ou plusieurs personnes n'existent pas")

        val roomExists = webClientService.checkRoomExists(dto.roomId).block() ?: false
        if (!roomExists) throw InvalidDataException("Salle inexistante")

        val existing = repository.findByRoomAndDayBetween(dto.roomId, dto.day, dto.day)
        if (existing.any { dto.start < it.end && dto.end > it.start }) {
            throw ReservationConflictException("Salle déjà réservée sur ce créneau")
        }

        val entity = dto.toEntity()
        return repository.save(entity)
    }

    fun getReservationById(id: UUID): ReservationEntity {
        return repository.findById(id) ?: throw NotFoundException("Réservation non trouvée")
    }

    fun findAll(roomId: Long?, dayStart: LocalDate?, dayEnd: LocalDate?): List<ReservationEntity> {
        if (dayStart != null && dayEnd != null && dayStart.isAfter(dayEnd)) {
            throw InvalidDataException("dayStart doit être inférieur ou égal à dayEnd")
        }
        return repository.findByRoomAndDayBetween(roomId, dayStart, dayEnd)
    }

    fun updateReservation(id: UUID, dto: ReservationDto): ReservationEntity {
        val existing = repository.findById(id) ?: throw NotFoundException("Réservation non trouvée")

        val peoplesExist = webClientService.checkAllPeoplesExist(dto.peoples).block() ?: false
        if (!peoplesExist) throw InvalidDataException("Une ou plusieurs personnes n'existent pas")

        val roomExists = webClientService.checkRoomExists(dto.roomId).block() ?: false
        if (!roomExists) throw InvalidDataException("Salle inexistante")

        val conflicts = repository.findByRoomAndDayBetween(dto.roomId, dto.day, dto.day)
            .filter { it.id != id }
        if (conflicts.any { dto.start < it.end && dto.end > it.start }) {
            throw ReservationConflictException("Salle déjà réservée sur ce créneau")
        }

        val updated = existing.copy(
            peoples = dto.peoples,
            roomId = dto.roomId,
            start = dto.start,
            end = dto.end,
            day = dto.day
        )
        return repository.save(updated)
    }

    fun deleteReservation(id: UUID): Boolean {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Réservation non trouvée")
        }
        return true
    }

    private fun ReservationDto.toEntity(): ReservationEntity =
        ReservationEntity(
            id = this.id ?: UUID.randomUUID(),
            ownerId = this.ownerId,
            peoples = this.peoples.toMutableList(),
            roomId = this.roomId,
            start = this.start,
            end = this.end,
            day = this.day
        )
}