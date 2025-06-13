package com.solara.data.repositories

import com.solara.data.networking.ApiService
import com.solara.data.networking.requests.ValidateSeedRequest
import com.solara.domain.model.Seed
import com.solara.domain.repositories.ConnectionErrorException
import com.solara.domain.repositories.QRRepository
import com.solara.domain.repositories.SeedServerException
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository implementation for QR-related operations.
 *
 * Handles error mapping from network to domain exceptions.
 */
internal class QRRepositoryImpl(
    private val apiService: ApiService,
) : QRRepository {

    /**
     * Fetches a new [Seed] from the API.
     *
     * @throws ConnectionErrorException on network issues.
     * @throws SeedServerException on HTTP errors.
     */
    override suspend fun getNewSeed(): Seed {
        try {
            return apiService.getNewSeed().toModel()
        } catch (e: Exception) {
            throw when (e) {
                is IOException -> ConnectionErrorException("Connection error", e)
                is HttpException -> SeedServerException("Server error", e)
                else -> e
            }
        }
    }


    override suspend fun validateSeed(value: String): Boolean {
        try {
            return apiService.validateSeed(ValidateSeedRequest(value)).valid
        } catch (e: Exception) {
            e.printStackTrace()

            throw when (e) {
                is IOException -> ConnectionErrorException("Connection error", e)
                is HttpException -> SeedServerException("Server error", e)
                else -> e
            }
        }
    }

}