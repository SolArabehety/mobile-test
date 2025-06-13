package com.solara.domain.usecases

import com.solara.core.utils.Result
import com.solara.domain.model.Seed
import com.solara.domain.model.SeedError
import com.solara.domain.repositories.ConnectionErrorException
import com.solara.domain.repositories.QRRepository
import com.solara.domain.repositories.SeedServerException


internal class GenerateSeedUseCaseImpl(
    private val qrRepository: QRRepository,
) : GenerateSeedUseCase {

    override suspend fun invoke(): Result<Seed, SeedError> {
        return try {
            Result.Success(qrRepository.getNewSeed())
        } catch (exception: ConnectionErrorException) {
            Result.Error(SeedError.NoInternetConnection)
        } catch (exception: SeedServerException) {
            Result.Error(SeedError.ServerError)
        } catch (e: Exception) {
            Result.Error(SeedError.Unknown)
        }

    }
}