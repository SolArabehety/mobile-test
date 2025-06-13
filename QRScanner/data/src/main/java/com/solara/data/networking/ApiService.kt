package com.solara.data.networking

import com.solara.data.networking.requests.ValidateSeedRequest
import com.solara.data.networking.responses.SeedResponse
import com.solara.data.networking.responses.ValidateSeedResponse
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Defines API endpoints for seed-related operations.
 */
interface ApiService {
    companion object {
        const val BASE_URL = "https://qrscanner-2amx.onrender.com"
    }

    /**
     * Fetches a new seed from the backend.
     *
     * @return [SeedResponse] with seed value and expiration date.
     */
    @POST("seed")
    suspend fun getNewSeed(): SeedResponse

    @POST("validate")
    suspend fun validateSeed(@Body seed: ValidateSeedRequest): ValidateSeedResponse

}