package com.store

import com.store.controllers.RequestInterceptor
import com.store.controllers.RequestWrappingFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import jakarta.servlet.Filter


@Configuration
open class WebConfig @Autowired constructor(
    private val customRequestInterceptor: RequestInterceptor,
    private val requestWrappingFilter: RequestWrappingFilter
) :
    WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(customRequestInterceptor).addPathPatterns("/products/**")
    }

    fun configureHttpFilters(filters: MutableList<Filter>) {
        filters.add(requestWrappingFilter)
    }
}