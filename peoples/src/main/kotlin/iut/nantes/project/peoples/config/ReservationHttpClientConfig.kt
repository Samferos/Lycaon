package iut.nantes.project.peoples.config

import iut.nantes.project.peoples.client.ReservationClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ReservationHttpClientConfig(
    private val props: ClientProperties
) {

    @Bean
    fun reservationsWebClient(builder: WebClient.Builder): WebClient =
        builder.baseUrl(props.reservations.baseUrl).defaultHeader("X-User", "INTERNAL_PEOPLE_SERVICE")
            .defaultHeader("X-THREAD", Thread.currentThread().name).build()

    @Bean
    fun reservationClient(reservationsWebClient: WebClient) = ReservationClient(reservationsWebClient)
}