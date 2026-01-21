package iut.nantes.project.bff.client

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class HttpClientConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.filter { request, next ->
                val auth = SecurityContextHolder.getContext().authentication

                val login = auth?.name ?: "anonymous"

                val filteredRequest = ClientRequest.from(request).header("X-User", login).build()

                next.exchange(filteredRequest)
            }.build()
    }
}