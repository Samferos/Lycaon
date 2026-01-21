package iut.nantes.project.reservations

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class XUserFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val xUser = httpRequest.getHeader("X-User")

        if (xUser.isNullOrBlank()) {
            httpResponse.status = HttpServletResponse.SC_FORBIDDEN
            httpResponse.writer.write("Access Denied: Please use the BFF gateway.")
            return
        }

        chain.doFilter(request, response)
    }
}