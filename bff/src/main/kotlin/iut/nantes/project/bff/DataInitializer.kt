package iut.nantes.project.bff

import iut.nantes.project.bff.dto.UserDto
import iut.nantes.project.bff.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val userService: UserService) : CommandLineRunner {

    override fun run(vararg args: String?) {
        try {
            userService.createUser(
                UserDto(login = "ADMIN", password = "ADMIN", isAdmin = true)
            )
            println("Utilisateur ADMIN/ADMIN créé avec succès.")
        } catch (e: Exception) {
            println("L'utilisateur ADMIN existe déjà.")
        }
    }
}