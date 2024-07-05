package com.store


import io.swagger.models.Swagger
import io.swagger.parser.SwaggerParser
import io.swagger.util.Json
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import java.io.File

object OpenApiValidator {

    private lateinit var swagger: Swagger

    init {
        loadSwaggerSpec()
    }

    private fun loadSwaggerSpec() {
        val swaggerFile = File("products_api.yaml") // Replace with your path
        println(swaggerFile.absolutePath)
        swagger = SwaggerParser().read(swaggerFile.absolutePath)
            ?: throw IllegalStateException("Failed to load Swagger specification")
        println(swagger)
    }

    fun validateRequestBody(requestPath: String, requestMethod: String, requestBody: String): Boolean {
        // Retrieve the corresponding operation from the Swagger specification
        val path = swagger.paths[requestPath] ?: return false
        val operation = findOperation(path, requestMethod) ?: return false

        // Get the request body schema
        val requestBodyModel = operation.parameters?.find { it is io.swagger.models.parameters.BodyParameter }?.let {
            (it as io.swagger.models.parameters.BodyParameter).schema
        } ?: return false

        // Convert the schema model to JSON object
        val schemaNode = Json.mapper().convertValue(requestBodyModel, JSONObject::class.java)
        val schema = SchemaLoader.load(schemaNode)

        // Parse and validate the request body
        return validateAgainstSchema(requestBody, schema)
    }

    private fun findOperation(path: io.swagger.models.Path, requestMethod: String): io.swagger.models.Operation? {
        return when (requestMethod.toUpperCase()) {
            "GET" -> path.get
            "POST" -> path.post
            "PUT" -> path.put
            "DELETE" -> path.delete
            "PATCH" -> path.patch
            "OPTIONS" -> path.options
            "HEAD" -> path.head
            else -> null
        }
    }

    private fun validateAgainstSchema(requestBody: String, schema: Schema): Boolean {
        return try {
            val json = JSONObject(requestBody)
            schema.validate(json)
            true
        } catch (e: Exception) {
            false
        }
    }
}
