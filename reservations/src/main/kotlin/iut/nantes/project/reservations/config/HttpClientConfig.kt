package iut.nantes.project.reservations.config

import iut.nantes.project.reservations.client.WebClientService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class HttpClientConfig(val props: ClientProperties) {

    @Bean
    fun roomsWebClient(builder: WebClient.Builder): WebClient =
        builder.baseUrl(props.rooms.baseUrl).defaultHeader("X-User", "INTERNAL_SYSTEM")
            .defaultHeader("X-THREAD", Thread.currentThread().name).build()

    @Bean
    fun peoplesWebClient(builder: WebClient.Builder): WebClient =
        builder.baseUrl(props.peoples.baseUrl).defaultHeader("X-User", "INTERNAL_SYSTEM")
            .defaultHeader("X-THREAD", Thread.currentThread().name).build()

    @Bean
    fun webClientService(
        peoplesWebClient: WebClient, roomsWebClient: WebClient
    ) = WebClientService(roomsWebClient, peoplesWebClient)
}

