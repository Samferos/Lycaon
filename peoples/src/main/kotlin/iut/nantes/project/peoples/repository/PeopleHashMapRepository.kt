package iut.nantes.project.peoples.repository

import iut.nantes.project.peoples.domain.People
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class PeopleHashMapRepository : PeopleRepository {

    private val data = ConcurrentHashMap<Long, People>()
    private val counter = AtomicLong(1)

    override fun save(people: People): People {
        val id = people.id.takeIf { it != 0L } ?: counter.getAndIncrement()
        val p = people.copy(id = id)
        data[id] = p
        return p
    }

    override fun findById(id: Long) = data[id]

    override fun findAll() = data.values.toList()

    override fun findByLastName(name: String) =
        data.values.filter { it.lastName.equals(name, ignoreCase = true) }

    override fun deleteById(id: Long) = data.remove(id) != null
}