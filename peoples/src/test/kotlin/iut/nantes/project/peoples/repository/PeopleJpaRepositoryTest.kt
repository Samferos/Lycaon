package iut.nantes.project.peoples.repository

import iut.nantes.project.peoples.domain.Address
import iut.nantes.project.peoples.domain.People
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class PeopleJpaRepositoryTest {

    @Autowired
    private lateinit var springRepo: PeopleJpaSpringRepository

    @Test
    fun `should save and find a person in database`() {
        val repository = PeopleJpaRepository(springRepo)
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
    fun `should return null when person does not exist`() {
        val repository = PeopleJpaRepository(springRepo)
        val found = repository.findById(999L)

        assertEquals(null, found)
    }

    @Test
    fun `should delete address when people is deleted`() {
        val repository = PeopleJpaRepository(springRepo)
        val person = People(
            id = 0,
            firstName = "Test",
            lastName = "Delete",
            age = 20,
            address = Address("Rue à supprimer", "Nantes", "44000", "France")
        )
        val saved = repository.save(person)
        val personId = saved.id

        repository.deleteById(personId)

        val found = repository.findById(personId)
        assertEquals(null, found)
    }
}