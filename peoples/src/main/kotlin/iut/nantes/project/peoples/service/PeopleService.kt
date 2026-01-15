package iut.nantes.project.peoples.service

import iut.nantes.project.peoples.controller.PeopleDto
import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.error.NotFoundException
import iut.nantes.project.peoples.mapper.toModel
import iut.nantes.project.peoples.repository.PeopleRepository

class PeopleService(private val database: PeopleRepository) {

    fun create(dto: PeopleDto): People {
        return database.save(dto.toModel())
    }

    fun getAll(name: String?): List<People> = if (name.isNullOrBlank()) database.findAll()
    else database.findByLastName(name)

    fun getById(id: Long): People = database.findById(id) ?: throw NotFoundException("Personne non trouvée")

    fun update(id: Long, dto: PeopleDto): People {
        val existing = getById(id)

        val updated = existing.copy(
            firstName = dto.firstName, lastName = dto.lastName, age = dto.age, address = existing.address.copy(
                street = dto.address.street,
                city = dto.address.city,
                zipCode = dto.address.zipCode,
                country = dto.address.country
            )
        )

        return database.save(updated)
    }

    fun delete(id: Long) {
        if (!database.deleteById(id)) {
            throw NotFoundException("Personne non trouvée")
        }
    }
}