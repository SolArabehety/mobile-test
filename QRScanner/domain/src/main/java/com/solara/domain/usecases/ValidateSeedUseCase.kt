package com.solara.domain.usecases

import com.solara.core.utils.Result
import com.solara.domain.model.SeedError


interface ValidateSeedUseCase {
    suspend operator fun invoke(value: String): Result<Boolean, SeedError>
}