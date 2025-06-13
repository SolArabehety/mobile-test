package com.solara.qrscanner.ui.di

import android.content.Context
import com.solara.qrscanner.ui.viewmodel.StringMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
internal object AppModule {

    @ViewModelScoped
    @Provides
    fun provideStringMapper(
        @ApplicationContext context: Context,
    ) = StringMapper(context.resources)

}