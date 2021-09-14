package edu.kamshanski.rubancfttask.model.utils

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

/** Gson Converter for [LocalDateTime] */
class LocalDateTimeJsonConverter: JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString() ?: LocalDateTime.MIN.toString())
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return if (json != null) {
            LocalDateTime.parse(json.asString, ISO_OFFSET_DATE_TIME)
        } else {
            LocalDateTime.MIN
        }
    }
}