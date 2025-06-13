package com.solara.data.networking.responses

import kotlinx.serialization.Serializable


@Serializable
data class ValidateSeedResponse(
    val valid: Boolean,
    val reason: String? = null
)
