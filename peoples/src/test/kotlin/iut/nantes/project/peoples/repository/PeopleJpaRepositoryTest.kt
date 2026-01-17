package iut.nantes.project.peoples.repository

import iut.nantes.project.peoples.client.ReservationClient
import iut.nantes.project.peoples.domain.Address
import iut.nantes.project.peoples.domain.People
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class PeopleJpaRepositoryTest {

    @Autowired
    private lateinit var springRepo: PeopleJpaSpringRepository

    private lateinit var repository: PeopleJpaRepository

    @BeforeEach
    fun setUp() {
        repository = PeopleJpaRepository(springRepo)
        springRepo.deleteAll()
    }

    @Test
    fun `should save and find a person by id`() {
        val person = People(
            id = 0,
            firstName = "Alice",
            lastName = "Zorg",
            age = 30,
            address = Address("10 rue test", "Nantes", "44000", "France")
        )

        val saved = repository.save(person)
        val found = repository.findById(saved.id)

        assertNotNull(found)
        assertEquals("Alice", found?.firstName)
        assertEquals("Zorg", found?.lastName)
        assertEquals("Nantes", found?.address?.city)
    }

    @Test
    fun `should find all people`() {
        val person1 =
            repository.save(People(0, "Alice", "Zorg", 30, Address("10 rue test", "Nantes", "44000", "France")))
        val person2 =
            repository.save(People(0, "Bob", "Marley", 35, Address("20 rue test", "Paris", "75000", "France")))

        val allPeople = repository.findAll()

        assertEquals(2, allPeople.size)
        assertTrue(allPeople.any { it.firstName == "Alice" })
        assertTrue(allPeople.any { it.firstName == "Bob" })
    }

    @Test
    fun `should find people by last name`() {
        repository.save(People(0, "Alice", "Zorg", 30, Address("10 rue test", "Nantes", "44000", "France")))
        repository.save(People(0, "Bob", "Zorg", 25, Address("20 rue test", "Paris", "75000", "France")))
        repository.save(People(0, "Charlie", "Marley", 35, Address("30 rue test", "Lyon", "69000", "France")))

        val zorgs = repository.findByLastName("Zorg")

        assertEquals(2, zorgs.size)
        assertTrue(zorgs.all { it.lastName == "Zorg" })
    }

    @Test
    fun `should delete person by id`() {
        val saved = repository.save(People(0, "Alice", "Zorg", 30, Address("10 rue test", "Nantes", "44000", "France")))

        val deleted = repository.deleteById(saved.id)
        val found = repository.findById(saved.id)

        assertTrue(deleted)
        assertNull(found)
    }

    @Test
    fun `should return false when deleting non-existent person`() {
        val deleted = repository.deleteById(999L)
        assertFalse(deleted)
    }

    @TestConfiguration
    class MockReservationClientConfig {
        @Bean
        fun reservationClient(): ReservationClient = mock(ReservationClient::class.java)
    }
}