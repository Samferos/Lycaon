package iut.nantes.project.bff.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    val login: String,

    val password: String,

    val role: String
)