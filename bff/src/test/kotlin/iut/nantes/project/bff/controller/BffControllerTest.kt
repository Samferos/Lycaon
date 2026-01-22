package iut.nantes.project.bff.controller

import iut.nantes.project.bff.client.ClientProperties
import iut.nantes.project.bff.config.SecurityConfig
import iut.nantes.project.bff.dto.*
import iut.nantes.project.bff.service.AggregationService
import iut.nantes.project.bff.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

@WebFluxTest(controllers = [BffController::class])
@Import(SecurityConfig::class)
@EnableConfigurationProperties(ClientProperties::class)
@AutoConfigureWebTestClient(timeout = "10000")
class BffControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var userService: UserService

    @MockitoBean
    private lateinit var aggregationService: AggregationService

    @MockitoBean
    private lateinit var webClient: WebClient

    @Test
    fun `createUser should return 201 when service succeeds`() {
        val newUser = UserDto("toto", "password", false)

        webTestClient.post().uri("/api/v1/user").contentType(MediaType.APPLICATION_JSON).bodyValue(newUser).exchange()
            .expectStatus().isCreated

        verify(userService).createUser(newUser)
    }

    @Test
    @WithMockUser
    fun `getPerson should return aggregated data`() {
        val personId = 1L
        val mockAggregated = PersonAggregatedDto(
            "Jean", "Dupont", 35, AddressDto("Rue", "Nantes", "44000", "FR"), listOf(UUID.randomUUID())
        )

        given(aggregationService.getPersonWithReservations(personId)).willReturn(Mono.just(mockAggregated))

        webTestClient.get().uri("/api/v1/peoples/$personId").exchange().expectStatus().isOk.expectBody()
            .jsonPath("$.firstName").isEqualTo("Jean").jsonPath("$.reservations").isArray
    }

    @Test
    @WithMockUser
    fun `getReservation should return aggregated reservation`() {
        val resId = UUID.randomUUID()
        val mockRes = ReservationAggregatedDto(
            resId,
            SimplePersonDto(1, "Jean", "Dupont"),
            listOf(),
            SimpleRoomDto(1, "Salle A"),
            10,
            12,
            java.time.LocalDate.now()
        )

        given(aggregationService.getFullReservation(resId)).willReturn(Mono.just(mockRes))

        webTestClient.get().uri("/api/v1/reservations/$resId").exchange().expectStatus().isOk.expectBody()
            .jsonPath("$.id").isEqualTo(resId.toString()).jsonPath("$.owner.firstName").isEqualTo("Jean")
    }
}