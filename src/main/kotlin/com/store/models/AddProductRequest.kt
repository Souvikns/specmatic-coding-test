package com.store.models


import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.jetbrains.annotations.NotNull
import java.io.IOException
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Positive

data class AddProductRequest(
    @field:NotNull @field:JsonDeserialize(using = StrictStringDeserializer::class) val name: String,
    @field:NotNull val type: String = "gadget",
    @field:NotNull @field:Min(1) @field:Max(9999) val inventory: Int = 1,
    @field:NotNull @field:Positive @field:Min(1) val cost: Int = 1,
)


class StrictStringDeserializer : StringDeserializer() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String? {
        val token = p.currentToken()
        if (token.isBoolean
            || token.isNumeric
            || !token.toString().equals("VALUE_STRING", ignoreCase = true)
        ) {
            ctxt.reportInputMismatch<Any>(String::class.java, "%s is not a `String` value!", token.toString())
            return null
        }
        return super.deserialize(p, ctxt)
    }
}