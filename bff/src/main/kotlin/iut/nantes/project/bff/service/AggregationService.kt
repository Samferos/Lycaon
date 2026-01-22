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
     * Si getPerson renvoie 404, le BFF renverra 404 automatiquement.
     */
    fun getPersonWithReservations(id: Long): Mono<PersonAggregatedDto> {
        return webClientService.getPerson(id).flatMap { person ->
            webClientService.getReservationsByOwner(id).map { it.id }.collectList().map { resIds ->
                    PersonAggregatedDto(person.firstName, person.lastName, person.age, person.address, resIds)
                }
        }
    }

    /**
     * GET /reservations/{id}
     * Ici, on applique le fallback DELETED si une personne manque.
     */
    fun getFullReservation(id: UUID): Mono<ReservationAggregatedDto> {
        return webClientService.getReservation(id).flatMap { resRaw ->

            val ownerMono = webClientService.getPerson(resRaw.ownerId)
                .map { SimplePersonDto(it.id, it.firstName, it.lastName) }
                .onErrorResume {
                    Mono.just(SimplePersonDto(resRaw.ownerId, "DELETED", "DELETED"))
                }

            val participantsMono = Flux.fromIterable(resRaw.peoples).flatMap { peopleId ->
                webClientService.getPerson(peopleId)
                    .map { SimplePersonDto(it.id, it.firstName, it.lastName) }
                    .onErrorResume {
                        Mono.just(SimplePersonDto(peopleId, "DELETED", "DELETED"))
                    }
            }.collectList()

            val roomMono = webClientService.getRoom(resRaw.roomId).map { SimpleRoomDto(it.id, it.name) }

            Mono.zip(ownerMono, participantsMono, roomMono).map { tuple ->
                ReservationAggregatedDto(
                    id = resRaw.id,
                    owner = tuple.t1,
                    peoples = tuple.t2,
                    roomId = tuple.t3,
                    start = resRaw.start,
                    end = resRaw.end,
                    day = resRaw.day
                )
            }
        }
    }
}