package iut.nantes.project.peoples.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import iut.nantes.project.peoples.client.ReservationClient
import iut.nantes.project.peoples.controller.AddressDto
import iut.nantes.project.peoples.controller.PeopleDto
import iut.nantes.project.peoples.domain.Address
import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.error.NotFoundException
import iut.nantes.project.peoples.repository.PeopleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PeopleServiceTest {
    private val repo = mockk<PeopleRepository>()
    private val reservationClient = mockk<ReservationClient>(relaxed = true)
    private val service = PeopleService(repo, reservationClient)

    private val samplePeople = People(
        1L, "Jean", "Dupont", 25, Address("Rue", "Nantes", "44000", "France")
    )

    @Test
    fun `create should return saved people`() {
        val dto = PeopleDto(
            firstName = "Jean", lastName = "Dupont", age = 25, address = AddressDto(
                street = "1 rue de la Paix", city = "Nantes", zipCode = "44000", country = "France"
            )
        )

        every { repo.save(any()) } returns samplePeople

        val result = service.create(dto)

        assertEquals("Jean", result.firstName)
        assertEquals(1L, result.id)

        verify(exactly = 1) { repo.save(any()) }
    }

    @Test
    fun `create - should map dto to model and return saved people`() {
        val dto = PeopleDto(
            firstName = "Jean",
            lastName = "Dupont",
            age = 25,
            address = AddressDto("Rue de la Paix", "Nantes", "44000", "France")
        )
        every { repo.save(any()) } returns samplePeople

        val result = service.create(dto)

        assertEquals(1L, result.id)
        assertEquals("Jean", result.firstName)
        verify(exactly = 1) { repo.save(any()) }
    }

    @Test
    fun `getById - should return people when exists`() {
        every { repo.findById(1L) } returns samplePeople

        val result = service.getById(1L)

        assertEquals(1L, result.id)
    }

    @Test
    fun `getById - should throw NotFoundException when id does not exist`() {
        every { repo.findById(99L) } returns null

        assertThrows<NotFoundException> { service.getById(99L) }
    }

    @Test
    fun `findAll - should return all people when name is null`() {
        every { repo.findAll() } returns listOf(samplePeople)

        val result = service.getAll(null)

        assertEquals(1, result.size)
        verify { repo.findAll() }
    }

    @Test
    fun `findAll - should search by name when name is provided`() {
        every { repo.findByLastName("Dupont") } returns listOf(samplePeople)

        val result = service.getAll("Dupont")

        assertEquals("Dupont", result[0].lastName)
        verify { repo.findByLastName("Dupont") }
    }

    @Test
    fun `update - should update existing fields and save`() {
        val updateDto = PeopleDto(
            firstName = "Jean-Claude",
            lastName = "Van Damme",
            age = 50,
            address = AddressDto("Nouvelle Rue", "Paris", "75000", "France")
        )

        every { repo.findById(1L) } returns samplePeople
        every { repo.save(any()) } returnsArgument 0

        val result = service.update(1L, updateDto)

        assertEquals("Jean-Claude", result.firstName)
        assertEquals("Van Damme", result.lastName)
        assertEquals("Nouvelle Rue", result.address.street)
        verify { repo.save(any()) }
    }

    @Test
    fun `delete - should succeed when id exists`() {
        every { repo.findById(1L) } returns samplePeople
        every { repo.deleteById(1L) } returns true
        every { reservationClient.deleteReservationsByOwner(1L) } returns Unit

        service.delete(1L)

        verify { repo.findById(1L) }
        verify { reservationClient.deleteReservationsByOwner(1L) }
        verify { repo.deleteById(1L) }
    }


    @Test
    fun `delete - should throw exception when id not found`() {
        every { repo.findById(99L) } returns null

        assertThrows<NotFoundException> { service.delete(99L) }

        verify { repo.findById(99L) }
    }

}