package com.store.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ObjectSchema
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.*


@Component
class RequestInterceptor : HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        println("Pre-Handle")

        val wrappedRequest = CachedBodyHttpServletRequest(request)
        val body = String(wrappedRequest.getBody())
        println("Request Body: $body")

        request.setAttribute("cachedBodyHttpServletRequest", wrappedRequest)
        val openapi = loadOpenApiSpec()
        val schema = openapi.components.schemas["ProductDetails"].toString()
        val sd = ObjectMapper().writeValueAsString(openapi.components.schemas["ProductDetails"])
        println(sd)
        val jsonSchema = SchemaLoader.builder().schemaJson(sd.trim()).draftV7Support().build().load().build()

        return validateAgainstSchema(body, jsonSchema)


    }

    private fun loadOpenApiSpec(): OpenAPI {
        val parser = OpenAPIParser()
        val result = parser.readLocation("./products_api.yaml", null, null)
        val openapi = result.openAPI

        if (openapi == null || openapi.openapi == null) {
            throw IllegalArgumentException("Failed to load OpenAPI spec")
        }
        return openapi
    }

    private fun validateAgainstSchema(requestBody: String, schema: Schema): Boolean {
        println(schema)
        return try {
            val json = JSONObject(requestBody)
            schema.validate(json)
            true
        } catch (e: Exception) {
            false
        }
    }

}


class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val body: ByteArray

    init {
        body = request.inputStream.readBytes()
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(body)
        return object : ServletInputStream() {
            override fun isFinished(): Boolean = byteArrayInputStream.available() == 0

            override fun isReady(): Boolean = true

            override fun setReadListener(readListener: ReadListener?) {
                // No implementation needed
            }

            override fun read(): Int = byteArrayInputStream.read()
        }
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    fun getBody(): ByteArray {
        return body
    }
}