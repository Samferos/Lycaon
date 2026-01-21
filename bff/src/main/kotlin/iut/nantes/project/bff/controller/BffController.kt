package iut.nantes.project.bff.controller

import iut.nantes.project.bff.client.ClientProperties
import iut.nantes.project.bff.dto.PersonAggregatedDto
import iut.nantes.project.bff.dto.ReservationAggregatedDto
import iut.nantes.project.bff.dto.UserDto
import iut.nantes.project.bff.service.AggregationService
import iut.nantes.project.bff.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class BffController(
    private val userService: UserService,
    private val aggregationService: AggregationService,
    private val webClient: WebClient,
    private val props: ClientProperties
) {

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody dto: UserDto) {
        userService.createUser(dto)
    }

    @GetMapping("/peoples/{id}")
    fun getPerson(@PathVariable id: Long): Mono<PersonAggregatedDto> {
        return aggregationService.getPersonWithReservations(id)
    }

    @GetMapping("/reservations/{id}")
    fun getReservation(@PathVariable id: UUID): Mono<ReservationAggregatedDto> {
        return aggregationService.getFullReservation(id)
    }

    @GetMapping("/peoples")
    fun findAllPeoples(@RequestParam(required = false) name: String?) =
        proxyGet("${props.peoples.baseUrl}/api/v1/peoples", mapOf("name" to (name ?: "")))

    @PostMapping("/peoples")
    fun createPeople(@RequestBody body: Any) = proxyPost("${props.peoples.baseUrl}/api/v1/peoples", body)

    @PutMapping("/peoples/{id}")
    fun updatePeople(@PathVariable id: Long, @RequestBody body: Any) =
        proxyPut("${props.peoples.baseUrl}/api/v1/peoples/$id", body)

    @DeleteMapping("/peoples/{id}")
    fun deletePeople(@PathVariable id: Long) = proxyDelete("${props.peoples.baseUrl}/api/v1/peoples/$id")

    @PutMapping("/peoples/{id}/address")
    fun updateAddress(@PathVariable id: Long, @RequestBody body: Any) =
        proxyPut("${props.peoples.baseUrl}/api/v1/peoples/$id/address", body)

    @GetMapping("/reservations")
    fun findAllReservations(@RequestParam params: Map<String, String>): Mono<ResponseEntity<Any>> =
        proxyGet("${props.reservations.baseUrl}/api/v1/reservations", params)

    @PostMapping("/reservations")
    fun createReservation(@RequestBody body: Any): Mono<ResponseEntity<Any>> =
        proxyPost("${props.reservations.baseUrl}/api/v1/reservations", body)

    @PutMapping("/reservations/{id}")
    fun updateReservation(@PathVariable id: UUID, @RequestBody body: Any): Mono<ResponseEntity<Any>> =
        proxyPut("${props.reservations.baseUrl}/api/v1/reservations/$id", body)

    @DeleteMapping("/reservations/{id}")
    fun deleteReservation(@PathVariable id: UUID): Mono<ResponseEntity<Any>> =
        proxyDelete("${props.reservations.baseUrl}/api/v1/reservations/$id")

    @GetMapping("/rooms")
    fun listAllRooms(): Mono<ResponseEntity<Any>> = proxyGet("${props.rooms.baseUrl}/api/v1/rooms", emptyMap())

    @GetMapping("/rooms/{id}")
    fun getRoomById(@PathVariable id: Long): Mono<ResponseEntity<Any>> =
        proxyGet("${props.rooms.baseUrl}/api/v1/rooms/$id", emptyMap())

    @PostMapping("/rooms")
    fun createRoom(@RequestBody body: Any): Mono<ResponseEntity<Any>> =
        proxyPost("${props.rooms.baseUrl}/api/v1/rooms", body)

    @PatchMapping("/rooms/{id}")
    fun updateRoomName(@PathVariable id: Long, @RequestBody body: Any): Mono<ResponseEntity<Any>> =
        proxyPatch("${props.rooms.baseUrl}/api/v1/rooms/$id", body)

    @DeleteMapping("/rooms/{id}")
    fun deleteRoom(@PathVariable id: Long): Mono<ResponseEntity<Any>> =
        proxyDelete("${props.rooms.baseUrl}/api/v1/rooms/$id")

    private fun proxyGet(uri: String, params: Map<String, String>) = webClient.get().uri { builder ->
            val baseUri = java.net.URI.create(uri)

            builder.scheme(baseUri.scheme).host(baseUri.host).port(baseUri.port).path(baseUri.path)

            params.forEach { (k, v) -> builder.queryParam(k, v) }
            builder.build()
        }.exchangeToMono { forwardResponse(it) }

    private fun proxyPost(uri: String, body: Any) =
        webClient.post().uri(uri).bodyValue(body).exchangeToMono { forwardResponse(it) }

    private fun proxyPut(uri: String, body: Any) =
        webClient.put().uri(uri).bodyValue(body).exchangeToMono { forwardResponse(it) }

    private fun proxyPatch(uri: String, body: Any) =
        webClient.patch().uri(uri).bodyValue(body).exchangeToMono { forwardResponse(it) }

    private fun proxyDelete(uri: String) = webClient.delete().uri(uri).exchangeToMono { forwardResponse(it) }

    private fun forwardResponse(response: ClientResponse) =
        response.bodyToMono<Any>().map { ResponseEntity.status(response.statusCode()).body(it) }
            .defaultIfEmpty(ResponseEntity.status(response.statusCode()).build())
}