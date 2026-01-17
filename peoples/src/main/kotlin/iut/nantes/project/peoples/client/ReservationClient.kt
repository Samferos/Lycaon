package iut.nantes.project.peoples.client

import iut.nantes.project.peoples.controller.ReservationSummary
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono

class ReservationClient(
    private val webClient: WebClient
) {

    fun deleteReservationsByOwner(ownerId: Long) {
        webClient.get().uri { uriBuilder ->
                uriBuilder.path("/reservations").queryParam("ownerId", ownerId).build()
            }.retrieve().bodyToFlux<ReservationSummary>().flatMap { reservation ->
                webClient.delete().uri("/reservations/{id}", reservation.id).retrieve().bodyToMono<Void>()
            }.blockLast()
    }
}