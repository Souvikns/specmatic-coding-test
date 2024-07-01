package com.store.models

import org.jetbrains.annotations.NotNull
import java.util.concurrent.atomic.AtomicInteger
import javax.validation.constraints.Min
import javax.validation.constraints.Positive

data class Product(
    @field:NotNull val name: String = "",
    @field:NotNull val type: String = "gadget",
    @field:Positive val inventory: Int = 0,
    @field:NotNull @field:Positive @field:Min(1) val cost: Int = 1,
    val id: Int = idGenerator.getAndIncrement()
) {
    companion object {
        val idGenerator = AtomicInteger(0)
    }
}