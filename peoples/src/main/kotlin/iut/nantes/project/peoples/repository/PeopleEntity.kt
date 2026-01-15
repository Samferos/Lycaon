package iut.nantes.project.peoples.repository

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Entity
data class PeopleEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @field:NotBlank @field:Size(min = 2, max = 20)
    val firstName: String,

    @field:NotBlank @field:Size(min = 2, max = 50)
    val lastName: String,

    @field:Min(18) @field:Max(119)
    val age: Int,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "address_id")
    val address: AddressEntity
)

@Entity
data class AddressEntity(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @field:NotBlank @field:Size(min = 5, max = 100)
    val street: String,

    @field:NotBlank @field:Size(min = 2, max = 50)
    val city: String,

    @field:Pattern(regexp = "\\d{5}")
    val zipCode: String,

    @field:NotBlank @field:Size(min = 2, max = 50)
    val country: String
)