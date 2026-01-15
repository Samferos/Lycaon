package iut.nantes.project.peoples.repository

import iut.nantes.project.peoples.domain.People

interface PeopleRepository {
    fun save(people: People): People
    fun findById(id: Long): People?
    fun findAll(): List<People>
    fun findByLastName(name: String): List<People>
    fun deleteById(id: Long): Boolean
}