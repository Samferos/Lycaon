package iut.nantes.project.reservations.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun peoplesClient(): WebClient = WebClient.builder().baseUrl("http://localhost:8081/api/v1/peoples").build()

    @Bean
    fun roomsClient(): WebClient = WebClient.builder().baseUrl("http://localhost:8083/api/v1/rooms").build()
}