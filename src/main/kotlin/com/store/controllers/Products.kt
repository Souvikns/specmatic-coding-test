package com.store.controllers

import com.store.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.store.models.*
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class Products(private val productService: ProductService) {

    @GetMapping("/products")
    fun findProducts(@Valid @RequestParam type: String = "other"): Any? {
        this.checkProductType(type)
        return ResponseEntity.ok(productService.getProducts(type))
    }

    @PostMapping("/products")
    fun addProducts(@Valid @RequestBody request: AddProductRequest ): ResponseEntity<Any>{
        this.checkProductType(request.type)
        val id = productService.addProduct(request)
        return ResponseEntity(AddProductResponse(id), HttpStatus.CREATED)
    }

    private fun checkProductType(type: String) {
        val pType = arrayListOf<String>("book", "food", "gadget", "other")
        if (type !in pType) {
            throw IllegalArgumentException("Invalid product type")
        }
    }

}

