package com.store

import com.store.models.AddProductRequest
import com.store.models.Product
import org.springframework.stereotype.Service
import javax.validation.Valid

@Service
class ProductService {
    private val products = mutableListOf<Product>()

    fun addProduct(@Valid product: AddProductRequest): Int {
        if (product.inventory < 1 || product.inventory > 9999) {
            throw IllegalArgumentException("Error")
        }
        if (product.cost <= 0) {
            throw IllegalArgumentException("Error")
        }
        val prd = Product(
            name = product.name,
            type = product.type,
            inventory = product.inventory,
            cost = product.cost,
        )
        this.products.add(prd)
        return prd.id
    }

    fun getProducts(type: String): List<Product> {
        val p = mutableListOf<Product>()
        for (product in products) {
            if (product.type == type) {
                p.add(product)
            }
        }
        return p
    }
}