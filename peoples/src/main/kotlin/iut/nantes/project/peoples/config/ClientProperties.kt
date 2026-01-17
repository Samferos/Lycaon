package iut.nantes.project.peoples.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("custom")
data class ClientProperties(
    val reservations: ClientEndpointProperties
)

data class ClientEndpointProperties(
    val baseUrl: String
)