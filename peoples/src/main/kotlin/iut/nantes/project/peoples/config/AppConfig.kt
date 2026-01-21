package iut.nantes.project.peoples.config

import iut.nantes.project.peoples.repository.*
import iut.nantes.project.peoples.service.PeopleService
import iut.nantes.project.peoples.XUserFilter
import iut.nantes.project.peoples.client.ReservationClient
import jakarta.servlet.Filter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class AppConfig {

    @Bean
    fun xUserFilter(): Filter = XUserFilter()

    @Bean
    @Profile("dev")
    fun peopleDatabaseDev(): PeopleRepository = PeopleHashMapRepository()

    @Bean
    @Profile("!dev")
    fun peopleDatabaseProd(springRepo: PeopleJpaSpringRepository): PeopleRepository =
        PeopleJpaRepository(springRepo)

    @Bean
    fun peopleService(
        database: PeopleRepository,
        reservationClient: ReservationClient
    ): PeopleService = PeopleService(database, reservationClient)
}