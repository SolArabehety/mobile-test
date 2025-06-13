package com.solara.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.solara.data.networking.ApiService
import com.solara.data.networking.serializers.DateSerializer
import com.solara.data.repositories.QRRepositoryImpl
import com.solara.domain.repositories.QRRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.util.Date
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal class DataHiltModule {

    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(Date::class, DateSerializer)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(ApiService.BASE_URL)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun providesQRRepository(apiService: ApiService): QRRepository =
        QRRepositoryImpl(
            apiService = apiService,
        )

}