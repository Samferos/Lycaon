package iut.nantes.project.reservations.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("custom")
data class ClientProperties(
    val rooms: ClientEndpointProperties,
    val peoples: ClientEndpointProperties
)

data class ClientEndpointProperties(val baseUrl: String)
