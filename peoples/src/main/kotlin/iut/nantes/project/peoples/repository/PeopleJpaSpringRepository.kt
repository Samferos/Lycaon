package iut.nantes.project.peoples.repository

import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.mapper.*
import org.springframework.data.jpa.repository.JpaRepository

interface PeopleJpaSpringRepository : JpaRepository<PeopleEntity, Long> {
    fun findByLastName(lastName: String): List<PeopleEntity>
}

class PeopleJpaRepository(private val springRepo: PeopleJpaSpringRepository) : PeopleRepository {
    override fun save(people: People): People {
        val entity = people.toEntity()
        val saved = springRepo.save(entity)
        return saved.toModel()
    }

    override fun findById(id: Long): People? =
        springRepo.findById(id).map { it.toModel() }.orElse(null)

    override fun findAll(): List<People> =
        springRepo.findAll().map { it.toModel() }

    override fun findByLastName(name: String): List<People> =
        springRepo.findByLastName(name).map { it.toModel() }

    override fun deleteById(id: Long): Boolean {
        if (!springRepo.existsById(id)) return false
        springRepo.deleteById(id)
        return true
    }
}
