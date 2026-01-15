package iut.nantes.project.reservations.client

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.format.DateTimeParseException

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to it.defaultMessage
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonErrors(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val message = if (ex.cause is DateTimeParseException) {
            "Le format de la date doit être yyyy-MM-dd"
        } else {
            "Format du corps de la requête invalide"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(InvalidDataException::class)
    fun handleInvalidData(ex: InvalidDataException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Données invalides")))
    }

    @ExceptionHandler(ReservationConflictException::class)
    fun handleConflict(ex: ReservationConflictException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to (ex.message ?: "Conflit de réservation")))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Ressource non trouvée")))
    }
}

class InvalidDataException(message: String) : RuntimeException(message)
class ReservationConflictException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)