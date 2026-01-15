package iut.nantes.project.peoples.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.*

data class AddressDto(
    @field:NotBlank(message = "La rue ne peut pas être vide") @field:Size(
        min = 5,
        max = 100,
        message = "La rue doit faire entre 5 et 100 caractères"
    ) val street: String,

    @field:NotBlank(message = "La ville ne peut pas être vide") @field:Size(
        min = 2,
        max = 50,
        message = "La ville doit faire entre 2 et 50 caractères"
    ) val city: String,

    @field:Pattern(regexp = "\\d{5}", message = "Le code postal doit avoir 5 chiffres") val zipCode: String,

    @field:NotBlank(message = "Le pays ne peut pas être vide") @field:Size(
        min = 2,
        max = 50,
        message = "Le pays doit faire entre 2 et 50 caractères"
    ) val country: String
)

data class PeopleDto(
    val id: Long? = null,

    @field:NotBlank @field:Size(
        min = 2, max = 20, message = "Le prenom doit être entre 2 et 20 caractères"
    ) val firstName: String,

    @field:NotBlank @field:Size(
        min = 2, max = 50, message = "Le nom doit être entre 2 et 50 caractères"
    ) val lastName: String,

    @field:Min(18, message = "L'age doit être supérieur ou égal à 18") @field:Max(
        119, message = "L'age doit être inférieur ou égal à 119"
    ) val age: Int,

    @field:Valid val address: AddressDto
)