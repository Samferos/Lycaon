package iut.nantes.project.peoples

import iut.nantes.project.peoples.client.ReservationClient
import iut.nantes.project.peoples.controller.PeopleController
import iut.nantes.project.peoples.repository.PeopleHashMapRepository
import iut.nantes.project.peoples.repository.PeopleJpaRepository
import iut.nantes.project.peoples.repository.PeopleJpaSpringRepository
import iut.nantes.project.peoples.repository.PeopleRepository
import iut.nantes.project.peoples.service.PeopleService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@SpringBootApplication
class PeoplesApplication {

    @Bean
    @Profile("dev")
    fun peopleDatabaseDev(): PeopleRepository = PeopleHashMapRepository()

    @Bean
    @Profile("!dev")
    fun peopleDatabaseProd(springRepo: PeopleJpaSpringRepository): PeopleRepository = PeopleJpaRepository(springRepo)

    @Bean
    fun peopleService(
        database: PeopleRepository, reservationClient: ReservationClient
    ): PeopleService = PeopleService(database, reservationClient)

    @Bean
    fun peopleController(service: PeopleService): PeopleController = PeopleController(service)
}

fun main(args: Array<String>) {
    runApplication<PeoplesApplication>(*args)
}


