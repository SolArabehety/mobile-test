package com.solara.domain.di

import com.solara.domain.repositories.QRRepository
import com.solara.domain.usecases.GenerateSeedUseCase
import com.solara.domain.usecases.GenerateSeedUseCaseImpl
import com.solara.domain.usecases.ValidateSeedUseCase
import com.solara.domain.usecases.ValidateSeedUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DomainHiltModule {
    @Provides
    @Singleton
    fun providesGenerateSeedUseCase(qrRepository: QRRepository): GenerateSeedUseCase =
        GenerateSeedUseCaseImpl(
            qrRepository = qrRepository,
        )


    @Provides
    @Singleton
    fun providesValidateSeedUseCase(qrRepository: QRRepository): ValidateSeedUseCase =
        ValidateSeedUseCaseImpl(
            qrRepository = qrRepository,
        )
}
