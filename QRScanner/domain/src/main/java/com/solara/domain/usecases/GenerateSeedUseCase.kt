package com.solara.domain.usecases

import com.solara.core.utils.Result
import com.solara.domain.model.Seed
import com.solara.domain.model.SeedError


/**
 * Use case for generating a new seed.
 */
interface GenerateSeedUseCase {
    /**
     * Triggers the seed generation flow.
     *
     * @return A [Result] with a [Seed] on success or [SeedError] on failure.
     */
    suspend operator fun invoke(): Result<Seed, SeedError>
}