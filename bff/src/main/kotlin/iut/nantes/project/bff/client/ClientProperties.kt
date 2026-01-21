package iut.nantes.project.bff.client

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
data class ClientProperties(
    val peoples: ServiceUrl,
    val rooms: ServiceUrl,
    val reservations: ServiceUrl
) {
    data class ServiceUrl(val baseUrl: String)
}