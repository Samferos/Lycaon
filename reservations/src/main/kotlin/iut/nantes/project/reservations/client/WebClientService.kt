package iut.nantes.project.reservations.client

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Profile("webclient")
class WebClientService(
    private val roomsWebClient: WebClient, private val peoplesWebClient: WebClient
) {

    fun checkPeopleExists(peopleId: Long): Mono<Boolean> {
        return peoplesWebClient.get().uri("/{id}", peopleId).retrieve().toBodilessEntity()
            .map { it.statusCode.is2xxSuccessful }.onErrorReturn(false)
    }

    fun checkRoomExists(roomId: Long): Mono<Boolean> {
        return roomsWebClient.get().uri("/rooms/{id}", roomId).retrieve().toBodilessEntity()
            .map { it.statusCode.is2xxSuccessful }.onErrorReturn(false)
    }

    fun checkAllPeoplesExist(peopleIds: List<Long>): Mono<Boolean> {
        return Flux.fromIterable(peopleIds).flatMap { checkPeopleExists(it) }.all { it }
    }
}