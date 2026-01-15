package iut.nantes.project.peoples.config

import iut.nantes.project.peoples.error.ApiErrorResponse
import iut.nantes.project.peoples.error.NotFoundException
import org.springframework.http.HttpStatusCode
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ApiErrorResponse(
                status = 404, message = e.message ?: "Ressource non trouvée"
            )
        )

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.badRequest().body(
            ApiErrorResponse(
                status = 400, message = e.message ?: "Violation de contrainte"
            )
        )

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity.badRequest().body(
            ApiErrorResponse(
                status = 400, message = message
            )
        )
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity.badRequest().body(
        ApiErrorResponse(
            status = 400, message = "Malformed request body"
        )
    )

    @ExceptionHandler(Exception::class)
    fun fallback(e: Exception): ResponseEntity<ApiErrorResponse> = ResponseEntity.internalServerError().body(
        ApiErrorResponse(
            status = 500, message = e.message ?: "Erreur interne"
        )
    )
}
