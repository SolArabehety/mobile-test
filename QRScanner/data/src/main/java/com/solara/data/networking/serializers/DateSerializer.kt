package com.solara.data.networking.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Custom Kotlinx serializer for java.util.Date
 *
 * Format: `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'`
 */
object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        val dateString = dateFormat.format(value)
        encoder.encodeString(dateString)
    }

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        return dateFormat.parse(dateString)!!
    }
}
