package com.solara.domain.repositories

import com.solara.domain.model.Seed


/**
 * Repository interface for managing seed operations.
 */
interface QRRepository {
    /**
     * Fetches a new [Seed] from the backend.
     */
    suspend fun getNewSeed(): Seed

    /**
     * Validates if the code is expired or not.
     */
    suspend fun validateSeed(value: String): Boolean
}

/**
 * Exception for network connectivity issues.
 */
class ConnectionErrorException(
    message: String,
    exception: Exception,
) : Exception(message, exception)

/**
 * Exception for HTTP/server-side errors when fetching a seed.
 */
class SeedServerException(
    message: String,
    exception: Exception,
) : Exception(message, exception)