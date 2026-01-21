package iut.nantes.project.bff.service

import iut.nantes.project.bff.dto.UserDto
import iut.nantes.project.bff.entity.UserEntity
import iut.nantes.project.bff.exception.LoginAlreadyUsedException
import iut.nantes.project.bff.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import java.util.concurrent.ConcurrentHashMap

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${bff.security:db}") private val securityMode: String
) : UserDetailsService {

    private val inMemoryUsers = ConcurrentHashMap<String, UserEntity>()

    override fun loadUserByUsername(username: String): UserDetails {
        val user = if (securityMode == "inmemory") {
            inMemoryUsers[username] ?: throw UsernameNotFoundException("User not found in memory")
        } else {
            userRepository.findById(username).orElseThrow { UsernameNotFoundException("User not found in DB") }
        }

        return User.withUsername(user.login).password(user.password).roles(user.role.replace("ROLE_", "")).build()
    }

    @Transactional
    fun createUser(dto: UserDto) {
        val role = if (dto.isAdmin) "ROLE_ADMIN" else "ROLE_USER"
        val entity = UserEntity(dto.login, passwordEncoder.encode(dto.password), role)

        if (securityMode == "inmemory") {
            if (inMemoryUsers.containsKey(dto.login)) throw LoginAlreadyUsedException("Login existant")
            inMemoryUsers[dto.login] = entity
        } else {
            if (userRepository.existsById(dto.login)) throw LoginAlreadyUsedException("Login existant")
            userRepository.save(entity)
        }
    }
}