package iut.nantes.project.peoples

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class XUserFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val xUser = httpRequest.getHeader("X-User")

        if (xUser.isNullOrBlank()) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Use BFF")
            return
        }
        chain.doFilter(request, response)
    }
}