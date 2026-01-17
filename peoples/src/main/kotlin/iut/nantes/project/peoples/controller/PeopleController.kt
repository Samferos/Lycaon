package iut.nantes.project.peoples.controller

import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.service.PeopleService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/peoples")
class PeopleController(
    private val peopleService: PeopleService
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: PeopleDto): ResponseEntity<People> {
        val created = peopleService.create(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping
    fun findAll(@RequestParam(required = false) name: String?): List<People> {
        return peopleService.getAll(name)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): People {
        return peopleService.getById(id)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: PeopleDto
    ): People {
        return peopleService.update(id, dto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        peopleService.delete(id)
    }

    @PutMapping("/{id}/address")
    fun updateAddress(
        @PathVariable id: Long,
        @Valid @RequestBody address: AddressDto
    ): People {
        return peopleService.updateAddress(id, address)
    }

}
