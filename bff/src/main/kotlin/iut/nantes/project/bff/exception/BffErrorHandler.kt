package iut.nantes.project.bff.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.bind.MethodArgumentNotValidException

@RestControllerAdvice
class BffErrorHandler {

    @ExceptionHandler(WebClientResponseException::class)
    fun handleWebClientError(ex: WebClientResponseException): ResponseEntity<Map<String, String>> {
        val message = ex.getResponseBodyAs(Map::class.java)?.get("error")?.toString() ?: ex.statusText
        return ResponseEntity.status(ex.statusCode).body(mapOf("error" to message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, String>> {
        val message = "Le format de l'identifiant '${ex.value}' est invalide."
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(LoginAlreadyUsedException::class)
    fun handleLoginUsed(ex: LoginAlreadyUsedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(mapOf("error" to (ex.message ?: "Ce login est déjà utilisé")))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Données invalides"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "Une erreur inattendue est survenue : ${ex.message}"))
    }
}