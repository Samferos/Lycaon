package iut.nantes.project.bff.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }.authorizeHttpRequests { auth ->
            auth.requestMatchers(HttpMethod.POST, "/api/v1/user").permitAll()

            auth.requestMatchers(HttpMethod.GET, "/api/v1/peoples/**").authenticated()
            auth.requestMatchers(HttpMethod.GET, "/api/v1/rooms/**").authenticated()
            auth.requestMatchers(HttpMethod.GET, "/api/v1/reservations/**").authenticated()

            auth.requestMatchers("/api/v1/peoples/**").hasRole("ADMIN")
            auth.requestMatchers("/api/v1/rooms/**").hasRole("ADMIN")

            auth.anyRequest().authenticated()
        }.httpBasic(Customizer.withDefaults())
        return http.build()
    }
}