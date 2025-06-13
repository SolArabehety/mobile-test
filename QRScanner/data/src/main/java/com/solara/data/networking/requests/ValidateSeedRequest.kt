package com.solara.data.networking.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidateSeedRequest(
    @SerialName("seed")
    val seed: String
)