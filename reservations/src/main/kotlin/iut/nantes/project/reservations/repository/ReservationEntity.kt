package iut.nantes.project.reservations.repository

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "reservation")
data class ReservationEntity(
    @Id
    val id: UUID,

    @Column(name = "owner_id", nullable = false)
    val ownerId: Long,

    @ElementCollection
    @CollectionTable(name = "reservation_peoples", joinColumns = [JoinColumn(name = "reservation_id")])
    @Column(name = "people_id")
    val peoples: List<Long> = emptyList(),

    @Column(name = "room_id", nullable = false)
    val roomId: Long,

    @Column(name = "start_hour", nullable = false)
    val start: Int,

    @Column(name = "end_hour", nullable = false)
    val end: Int,

    @Column(name = "reservation_day", nullable = false)
    val day: LocalDate
)
