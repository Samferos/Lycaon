package iut.nantes.project.peoples.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import iut.nantes.project.peoples.domain.Address
import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.error.NotFoundException
import iut.nantes.project.peoples.service.PeopleService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PeopleController::class)
@ActiveProfiles("dev")
class PeopleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var peopleService: PeopleService

    private val samplePeople = People(
        1L, "Jean", "Dupont", 25, Address("1 rue de la Paix", "Nantes", "44000", "France")
    )

    @Test
    fun `getAll - should return list of people`() {
        every { peopleService.getAll(null) } returns listOf(samplePeople)

        mockMvc.perform(get("/api/v1/peoples")).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].firstName").value("Jean"))
    }

    @Test
    fun `getById - should return 200 when found`() {
        every { peopleService.getById(1L) } returns samplePeople

        mockMvc.perform(get("/api/v1/peoples/1")).andExpect(status().isOk)
            .andExpect(jsonPath("$.lastName").value("Dupont"))
    }

    @Test
    fun `getById - should return 404 when not found`() {
        every { peopleService.getById(99L) } throws NotFoundException("Personne non trouvée")

        mockMvc.perform(get("/api/v1/peoples/99")).andExpect(status().isNotFound)
    }

    @Test
    fun `create - should return 201 when valid`() {
        val dto = createValidDto()
        every { peopleService.create(any()) } returns samplePeople

        mockMvc.perform(
            post("/api/v1/peoples").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `create - should return 400 when age is too low`() {
        val invalidDto = createValidDto().copy(age = 15) // Moins de 18 ans

        mockMvc.perform(
            post("/api/v1/peoples").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `update - should return 200 when successful`() {
        val dto = createValidDto()
        every { peopleService.update(eq(1L), any()) } returns samplePeople

        mockMvc.perform(
            put("/api/v1/peoples/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk)
    }

    @Test
    fun `delete - should return 204`() {
        every { peopleService.delete(1L) } returns Unit

        mockMvc.perform(delete("/api/v1/peoples/1")).andExpect(status().isNoContent)
    }

    private fun createValidDto() = PeopleDto(
        firstName = "Jean",
        lastName = "Dupont",
        age = 25,
        address = AddressDto("1 rue de la Paix", "Nantes", "44000", "France")
    )
}