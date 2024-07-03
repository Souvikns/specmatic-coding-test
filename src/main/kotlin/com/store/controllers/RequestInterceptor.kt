package com.store.controllers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.micrometer.core.instrument.util.IOUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths


@Component
class RequestInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestBody = IOUtils.toString(request.inputStream, StandardCharsets.UTF_8)
        println(requestBody)
        try{

            val schema = this.loadYamlFromFile("./products_api.yaml")
            println(schema)

            val schemaLoader = SchemaLoader.builder().schemaJson(schema).build()
            val reqJson = JSONObject(requestBody.toString().trim())
            schemaLoader.load().build().validate(reqJson)
            println("request valid")
            return true
        }catch (e: ValidationException){
            println(e)
            return false
        } catch (e: Exception) {
            println(e)
            return false
        }
    }

    private fun loadYamlFromFile(filePath: String): Map<String, Any>? {
        try {
            val file = File(filePath)
            val inputStream = FileInputStream(file)
            val yaml = Yaml()
            @Suppress("UNCHECKED_CAST")
            return yaml.load(inputStream) as Map<String, Any>?
        } catch (e: FileNotFoundException) {
            println("File not found: $filePath")
        } catch (e: Exception) {
            println("Error reading YAML from file: ${e.message}")
        }
        return null
    }
}