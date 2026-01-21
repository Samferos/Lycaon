package iut.nantes.project.bff.client

import iut.nantes.project.bff.dto.AddressDto
import iut.nantes.project.bff.dto.PersonRawDto
import iut.nantes.project.bff.dto.ReservationRawDto
import iut.nantes.project.bff.dto.RoomRawDto
import org.springframework.stereotype.Service
import org.springframework.web.method.support.CompositeUriComponentsContributor
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class WebClientService(
    private val webClient: WebClient,
    private val props: ClientProperties,
) {

    fun getPerson(id: Long): Mono<PersonRawDto> {
        return webClient.get().uri("${props.peoples.baseUrl}/api/v1/peoples/{id}", id).retrieve()
            .bodyToMono<PersonRawDto>().onErrorResume { _ ->
                Mono.just(
                    PersonRawDto(
                        id = id,
                        firstName = "DELETED",
                        lastName = "DELETED",
                        age = 0,
                        address = AddressDto("", "", "", "")
                    )
                )
            }
    }

    fun getRoom(id: Long): Mono<RoomRawDto> {
        return webClient.get().uri("${props.rooms.baseUrl}/api/v1/rooms/{id}", id).retrieve().bodyToMono<RoomRawDto>()
    }

    fun getReservation(id: UUID): Mono<ReservationRawDto> {
        return webClient.get().uri("${props.reservations.baseUrl}/api/v1/reservations/{id}", id).retrieve()
            .onStatus({ it.is4xxClientError }) { response ->
                response.bodyToMono(Map::class.java).flatMap { body ->
                    val remoteError = body["error"]?.toString() ?: "Données invalides"
                    Mono.error(ResponseStatusException(response.statusCode(), remoteError))
                }
            }.bodyToMono<ReservationRawDto>()
    }

    fun getAllReservations(): Flux<ReservationRawDto> {
        return webClient.get().uri("${props.reservations.baseUrl}/api/v1/reservations").retrieve()
            .bodyToFlux<ReservationRawDto>()
    }
}