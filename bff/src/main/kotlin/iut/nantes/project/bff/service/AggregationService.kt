package iut.nantes.project.bff.service

import iut.nantes.project.bff.client.WebClientService
import iut.nantes.project.bff.dto.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class AggregationService(
    private val webClientService: WebClientService
) {

    /**
     * GET /peoples/{id}
     * Récupère une personne et la liste des IDs de ses réservations (owner).
     */
    fun getPersonWithReservations(id: Long): Mono<PersonAggregatedDto> {
        val personMono = webClientService.getPerson(id)

        val reservationsMono =
            webClientService.getAllReservations().filter { it.ownerId == id }.map { it.id }.collectList()

        return Mono.zip(personMono, reservationsMono).map { tuple ->
            val person = tuple.t1
            val resIds = tuple.t2

            PersonAggregatedDto(
                firstName = person.firstName,
                lastName = person.lastName,
                age = person.age,
                address = person.address,
                reservations = resIds
            )
        }
    }

    /**
     * GET /reservations/{id}
     * Agrège Reservation + Owner + Participants + Room
     */
    fun getFullReservation(id: UUID): Mono<ReservationAggregatedDto> {
        return webClientService.getReservation(id).flatMap { resRaw ->

            val ownerMono =
                webClientService.getPerson(resRaw.ownerId).map { SimplePersonDto(it.id, it.firstName, it.lastName) }

            val roomMono = webClientService.getRoom(resRaw.roomId).map { SimpleRoomDto(it.id, it.name) }

            val participantsMono = Flux.fromIterable(resRaw.peoples).flatMap { peopleId ->
                    webClientService.getPerson(peopleId).map { SimplePersonDto(it.id, it.firstName, it.lastName) }
                }.collectList()

            Mono.zip(ownerMono, participantsMono, roomMono).map { tuple ->
                ReservationAggregatedDto(
                    id = resRaw.id, owner = tuple.t1,       // Owner
                    peoples = tuple.t2,     // Participants
                    roomId = tuple.t3,      // Room
                    start = resRaw.start, end = resRaw.end, day = resRaw.day
                )
            }
        }
    }
}