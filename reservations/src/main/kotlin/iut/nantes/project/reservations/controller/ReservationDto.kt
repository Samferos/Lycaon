package iut.nantes.project.reservations.controller

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.util.UUID

data class ReservationDto(
    val id: UUID? = null,

    @field:NotNull(message = "L'ID de l'owner est obligatoire")
    @field:JsonProperty("owner")
    val ownerId: Long,

    @field:NotEmpty(message = "La liste des personnes ne peut pas être vide") val peoples: List<Long>,

    @field:NotNull(message = "L'ID de la salle est obligatoire") val roomId: Long,

    @field:Min(value = 0, message = "L'heure de début doit être entre 0 et 23") @field:Max(
        value = 23,
        message = "L'heure de début doit être entre 0 et 23"
    ) val start: Int,

    @field:Min(value = 1, message = "L'heure de fin doit être entre 1 et 24") @field:Max(
        value = 24,
        message = "L'heure de fin doit être entre 1 et 24"
    ) val end: Int,

    @field:FutureOrPresent(message = "La date ne peut pas être dans le passé")
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate
)

data class PeoplesDto(val id: Long)