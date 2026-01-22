package iut.nantes.project.reservations.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    private val mockServer: ClientAndServer = startClientAndServer(8888)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @BeforeEach
    fun reset() {
        mockServer.reset()
    }

    @AfterAll
    fun stopServer() {
        mockServer.stop()
    }

    @Test
    fun `should create reservation when all services return 200`() {
        val ownerId = 4L
        val participantIds = listOf(1L, 2L, 3L)
        val roomId = 42L

        (participantIds + ownerId).forEach { id ->
            mockServer.`when`(request().withMethod("GET").withPath("/peoples/$id")).respond(
                    response().withStatusCode(200).withHeader("Content-Type", "application/json")
                        .withBody("""{"id": $id}""")
                )
        }

        mockServer.`when`(request().withMethod("GET").withPath("/rooms/$roomId")).respond(
                response().withStatusCode(200).withHeader("Content-Type", "application/json")
                    .withBody("""{"id": $roomId}""")
            )

        val reservationJson = """
            {
              "owner": $ownerId,
              "peoples": [1, 2, 3],
              "roomId": $roomId,
              "start": 8,
              "end": 10,
              "day": "2026-12-25"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/reservations").contentType(MediaType.APPLICATION_JSON).content(reservationJson)
        ).andExpect(status().isCreated).andExpect(jsonPath("$.ownerId").value(ownerId))
            .andExpect(jsonPath("$.roomId").value(roomId))
    }

    @Test
    fun `should return 400 when owner does not exist`() {
        mockServer.`when`(request().withMethod("GET").withPath("/api/v1/peoples/99"))
            .respond(response().withStatusCode(404))

        val reservationJson = """
            {
              "owner": 99,
              "peoples": [1],
              "roomId": 42,
              "start": 8,
              "end": 9,
              "day": "2026-12-25"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/reservations").contentType(MediaType.APPLICATION_JSON).content(reservationJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 400 when start time is after end time`() {
        val reservationJson = """
            {
              "owner": 4,
              "peoples": [1],
              "roomId": 42,
              "start": 10,
              "end": 8,
              "day": "2026-12-25"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/reservations").contentType(MediaType.APPLICATION_JSON).content(reservationJson)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `should return 404 when reservation id is unknown`() {
        val randomId = UUID.randomUUID()

        mockMvc.perform(get("/api/v1/reservations/$randomId")).andExpect(status().isNotFound)
    }
}