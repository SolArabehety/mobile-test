package com.solara.domain.usecases

import com.solara.core.utils.Result
import com.solara.domain.model.SeedError
import com.solara.domain.repositories.ConnectionErrorException
import com.solara.domain.repositories.QRRepository
import com.solara.domain.repositories.SeedServerException


internal class ValidateSeedUseCaseImpl(
    private val qrRepository: QRRepository,
) : ValidateSeedUseCase {

    override suspend fun invoke(value: String): Result<Boolean, SeedError> {
        return try {
            Result.Success(qrRepository.validateSeed(value))
        } catch (exception: ConnectionErrorException) {
            Result.Error(SeedError.NoInternetConnection)
        } catch (exception: SeedServerException) {
            Result.Error(SeedError.ServerError)
        } catch (e: Exception) {
            Result.Error(SeedError.Unknown)
        }

    }
}