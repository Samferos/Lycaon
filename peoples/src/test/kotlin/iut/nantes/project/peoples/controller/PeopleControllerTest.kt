package iut.nantes.project.peoples.controller

import com.fasterxml.jackson.databind.ObjectMapper
import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.repository.PeopleJpaSpringRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
class PeopleControllerTest {

    private val mockServer: ClientAndServer = startClientAndServer(8888)

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun resetDb(@Autowired peopleJpaRepo: PeopleJpaSpringRepository) {
        peopleJpaRepo.deleteAll()
        mockServer.reset()
    }

    @AfterAll
    fun stopServer() {
        mockServer.stop()
    }

    private fun createValidDto() = PeopleDto(
        firstName = "Jean",
        lastName = "Dupont",
        age = 25,
        address = AddressDto("1 rue de la Paix", "Nantes", "44000", "France")
    )

    private fun mockDeleteReservations(ownerId: Long) {
        mockServer.`when`(
            request().withMethod("DELETE").withPath("/api/v1/reservations/owner/$ownerId")
        ).respond(response().withStatusCode(204))
    }

    @Test
    fun `getAll - should return list of people`() {
        val dto = createValidDto()
        mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect { status { isCreated() } }

        mockMvc.get("/api/v1/peoples").andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].firstName") { value("Jean") }
        }
    }

    @Test
    fun `getById - should return 200 when found`() {
        val dto = createValidDto()
        val result = mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andReturn()

        val created: People = objectMapper.readValue(result.response.contentAsString, People::class.java)

        mockMvc.get("/api/v1/peoples/${created.id}").andExpect {
            status { isOk() }
            jsonPath("$.lastName") { value("Dupont") }
        }
    }

    @Test
    fun `getById - should return 404 when not found`() {
        mockMvc.get("/api/v1/peoples/999").andExpect { status { isNotFound() } }
    }

    @Test
    fun `create - should return 201 when valid`() {
        val dto = createValidDto()
        mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.firstName") { value("Jean") }
        }
    }

    @Test
    fun `create - should return 400 when age is too low`() {
        val invalidDto = createValidDto().copy(age = 15)
        mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidDto)
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun `update - should return 200 when successful`() {
        val dto = createValidDto()
        val result = mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andReturn()

        val created: People = objectMapper.readValue(result.response.contentAsString, People::class.java)
        val updateDto = dto.copy(firstName = "Paul")

        mockMvc.put("/api/v1/peoples/${created.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateDto)
        }.andExpect { status { isOk() } }

        mockMvc.get("/api/v1/peoples/${created.id}").andExpect {
            status { isOk() }
            jsonPath("$.firstName") { value("Paul") }
        }
    }

    @Test
    fun `delete - should return 204`() {
        val dto = createValidDto()
        val result = mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andReturn()

        val created: People = objectMapper.readValue(result.response.contentAsString, People::class.java)

        mockGetReservations(created.id)

        mockMvc.delete("/api/v1/peoples/${created.id}").andExpect { status { isNoContent() } }

        mockMvc.get("/api/v1/peoples/${created.id}").andExpect { status { isNotFound() } }
    }


    @Test
    fun `updateAddress - should update only address`() {
        val dto = createValidDto()
        val result = mockMvc.post("/api/v1/peoples") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andReturn()

        val created: People = objectMapper.readValue(result.response.contentAsString, People::class.java)
        val newAddress = AddressDto("2 rue Nouvelle", "Paris", "75000", "France")

        mockMvc.put("/api/v1/peoples/${created.id}/address") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newAddress)
        }.andExpect { status { isOk() } }

        mockMvc.get("/api/v1/peoples/${created.id}").andExpect {
            status { isOk() }
            jsonPath("$.address.street") { value("2 rue Nouvelle") }
            jsonPath("$.firstName") { value("Jean") }
        }
    }

    private fun mockGetReservations(ownerId: Long) {
        mockServer.`when`(
            request().withMethod("GET").withPath("/reservations")
                .withQueryStringParameter("ownerId", ownerId.toString())
        ).respond(
            response().withStatusCode(204)
        )
    }

}

