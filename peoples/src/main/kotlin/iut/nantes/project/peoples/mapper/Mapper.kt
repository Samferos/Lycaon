package iut.nantes.project.peoples.mapper

import iut.nantes.project.peoples.controller.PeopleDto
import iut.nantes.project.peoples.domain.Address
import iut.nantes.project.peoples.domain.People
import iut.nantes.project.peoples.repository.AddressEntity
import iut.nantes.project.peoples.repository.PeopleEntity

fun PeopleEntity.toModel(): People =
    People(
        id = this.id!!,
        firstName = this.firstName,
        lastName = this.lastName,
        age = this.age,
        address = this.address.toModel()
    )

fun AddressEntity.toModel(): Address =
    Address(
        street = this.street,
        city = this.city,
        zipCode = this.zipCode,
        country = this.country
    )

fun People.toEntity(): PeopleEntity =
    PeopleEntity(
        id = this.id.takeIf { it != 0L },
        firstName = this.firstName,
        lastName = this.lastName,
        age = this.age,
        address = this.address.toEntity()
    )

fun Address.toEntity(): AddressEntity =
    AddressEntity(
        street = this.street,
        city = this.city,
        zipCode = this.zipCode,
        country = this.country
    )

fun PeopleDto.toModel(): People =
    People(
        id = 0L,
        firstName = this.firstName,
        lastName = this.lastName,
        age = this.age,
        address = Address(
            street = this.address.street,
            city = this.address.city,
            zipCode = this.address.zipCode,
            country = this.address.country
        )
    )