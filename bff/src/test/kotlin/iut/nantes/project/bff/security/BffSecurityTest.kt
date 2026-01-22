package iut.nantes.project.bff.security

import iut.nantes.project.bff.client.ClientProperties
import iut.nantes.project.bff.client.WebClientService
import iut.nantes.project.bff.config.SecurityConfig
import iut.nantes.project.bff.controller.BffController
import iut.nantes.project.bff.service.AggregationService
import iut.nantes.project.bff.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.function.Function

@WebFluxTest(controllers = [BffController::class])
@Import(SecurityConfig::class)
@EnableConfigurationProperties(ClientProperties::class)
@TestPropertySource(
    properties = ["custom.peoples.base-url=http://localhost:8081", "custom.rooms.base-url=http://localhost:8082", "custom.reservations.base-url=http://localhost:8083"]
)
class BffSecurityTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var aggregationService: AggregationService

    @MockitoBean
    private lateinit var webClientService: WebClientService

    @MockitoBean
    private lateinit var webClient: WebClient

    @Test
    fun `POST user should be public (No Auth)`() {
        webTestClient.mutateWith(csrf()).post().uri("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"login":"new", "password":"pw", "isAdmin":false}""").exchange().expectStatus().isCreated
    }

    @Test
    fun `GET peoples should fail without Auth`() {
        webTestClient.get().uri("/api/v1/peoples/1").exchange().expectStatus().isUnauthorized
    }

    @Test
    @WithMockUser(username = "toto", roles = ["USER"])
    fun `GET peoples should succeed with USER role`() {
        webTestClient.get().uri("/api/v1/peoples/1").exchange().expectStatus().isOk
    }

    @Test
    @WithMockUser(username = "toto", roles = ["USER"])
    fun `DELETE peoples should fail with USER role`() {
        webTestClient.mutateWith(csrf()).delete().uri("/api/v1/peoples/1").exchange().expectStatus().isForbidden
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `DELETE peoples should succeed with ADMIN role`() {

        webTestClient.mutateWith(csrf()).delete().uri("/api/v1/peoples/1").exchange().expectStatus().is5xxServerError
    }

    @Test
    @WithMockUser(username = "admin", roles = ["ADMIN"])
    fun `DELETE peoples should succeed with ADMIN role2`() {
        val uriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
        val headerSpec = mock(WebClient.RequestHeadersSpec::class.java)

        given(webClient.delete()).willReturn(uriSpec)
        given(uriSpec.uri(ArgumentMatchers.anyString())).willReturn(headerSpec)

        given(headerSpec.exchangeToMono<ResponseEntity<Void>>(any())).willReturn(Mono.just(ResponseEntity.ok().build()))


        webTestClient.mutateWith(csrf()).delete().uri("/api/v1/peoples/1").exchange().expectStatus().isOk
    }

}