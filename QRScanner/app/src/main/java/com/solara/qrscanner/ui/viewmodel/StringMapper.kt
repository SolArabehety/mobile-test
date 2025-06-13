package com.solara.qrscanner.ui.viewmodel

import android.content.res.Resources
import com.solara.domain.model.SeedError
import com.solara.qrscanner.R
import javax.inject.Inject

internal class StringMapper @Inject constructor(
    private val resources: Resources,
) {

    fun mapErrorString(error: SeedError) = when (error) {
        SeedError.NoInternetConnection ->resources.getString(R.string.no_internet_connection)
        SeedError.ServerError -> resources.getString(R.string.server_error)
        SeedError.ExpiredToken ->  resources.getString(R.string.expired_token)
        SeedError.Unknown ->  resources.getString(R.string.generic_error)
        SeedError.InvalidQRGeneration -> resources.getString(R.string.invalid_qr_generation)
    }
}
