package com.solara.domain.model


/**
 * Enum representing possible seed-related errors.
 */
enum class SeedError {
    NoInternetConnection,
    ServerError,
    ExpiredToken,
    InvalidQRGeneration,
    Unknown,
}
