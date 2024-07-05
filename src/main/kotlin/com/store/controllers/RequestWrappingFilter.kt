package com.store.controllers




import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class RequestWrappingFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val wrappedRequest = CachedBodyHttpServletRequest(request as HttpServletRequest)
        chain.doFilter(wrappedRequest, response)
    }

    override fun init(filterConfig: FilterConfig) {
        // No initialization needed
    }

    override fun destroy() {
        // No cleanup needed
    }
}