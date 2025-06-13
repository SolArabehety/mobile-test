package com.solara.data.networking.responses

import com.solara.domain.model.Seed
import com.solara.data.networking.serializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import java.util.Date

/**
 * DTO for the seed response returned by the API.
 *
 * @property seed Raw seed string.
 * @property expiredDate UTC expiration date.
 */
@Serializable
data class SeedResponse(
    @SerialName("seed")
    val seed: String,

    @SerialName("expires_at")
    @Serializable(with = DateSerializer::class)
    val expiredDate: Date,
) {

    /**
     * Maps [SeedResponse] to domain [Seed] model.
     */
    fun toModel() = Seed(
        value = seed,
        expiredDate = expiredDate
    )
}