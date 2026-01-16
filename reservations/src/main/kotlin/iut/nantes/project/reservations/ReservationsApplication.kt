package iut.nantes.project.reservations

import iut.nantes.project.reservations.client.WebClientService
import iut.nantes.project.reservations.config.ClientProperties
import iut.nantes.project.reservations.repository.ReservationHashMapRepository
import iut.nantes.project.reservations.repository.ReservationRepository
import iut.nantes.project.reservations.service.ReservationService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableConfigurationProperties(ClientProperties::class)
class ReservationsApplication {

    @Bean
    @Profile("dev")
    fun reservationRepositoryDev(): ReservationRepository = ReservationHashMapRepository()

    @Bean
    fun reservationService(
        repository: ReservationRepository,
        webClientService: WebClientService
    ) = ReservationService(repository, webClientService)
}

fun main(args: Array<String>) {
    runApplication<ReservationsApplication>(*args)
}
