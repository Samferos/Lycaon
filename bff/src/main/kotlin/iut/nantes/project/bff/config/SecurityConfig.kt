package iut.nantes.project.bff.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf { it.disable() }.authorizeExchange { auth ->
            auth.pathMatchers(HttpMethod.POST, "/api/v1/user").permitAll()

            auth.pathMatchers(HttpMethod.GET, "/api/v1/peoples/**").authenticated()
            auth.pathMatchers(HttpMethod.GET, "/api/v1/rooms/**").authenticated()
            auth.pathMatchers(HttpMethod.GET, "/api/v1/reservations/**").authenticated()

            auth.pathMatchers("/api/v1/peoples/**").hasRole("ADMIN")
            auth.pathMatchers("/api/v1/rooms/**").hasRole("ADMIN")

            auth.anyExchange().authenticated()
        }.httpBasic(Customizer.withDefaults())
        return http.build()
    }
}