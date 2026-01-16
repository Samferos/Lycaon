package iut.nantes.project.reservations.controller

import iut.nantes.project.reservations.repository.ReservationEntity
import iut.nantes.project.reservations.service.ReservationService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/v1/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {

    /** POST /api/v1/reservations */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody dto: ReservationDto): ReservationEntity {
        return reservationService.createReservation(dto)
    }

    /** GET /api/v1/reservations/{id} */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ReservationEntity {
        return reservationService.getReservationById(id)
    }

    /**
     * GET /api/v1/reservations?roomId=1&dayStart=2025-12-25&dayEnd=2025-12-26
     * Tous les filtres sont optionnels
     */
    @GetMapping
    fun findAll(
        @RequestParam(required = false) roomId: Long?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dayStart: LocalDate?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dayEnd: LocalDate?
    ): List<ReservationEntity> {
        return reservationService.findAll(roomId, dayStart, dayEnd)
    }

    /** PUT /api/v1/reservations/{id} */
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: ReservationDto
    ): ReservationEntity {
        return reservationService.updateReservation(id, dto)
    }

    /** DELETE /api/v1/reservations/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        reservationService.deleteReservation(id)
    }
}
