package com.solara.qrscanner.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solara.core.onError
import com.solara.core.onSuccess
import com.solara.core.utils.QrCodeGenerator
import com.solara.domain.model.Seed
import com.solara.domain.model.SeedError
import com.solara.domain.usecases.GenerateSeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject


@HiltViewModel
internal class CreateQRViewModel @Inject constructor(
    private val stringMapper: StringMapper,
    private val qrCodeGenerator: QrCodeGenerator,
    private val generateSeedUseCase: GenerateSeedUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "CreateQRViewModel"
        private const val BITMAP_SIZE = 500
        private const val COUNTDOWN_INTERVAL_MS = 1000L
    }

    private val _uiState = MutableStateFlow<CreateQRUiState>(CreateQRUiState.Loading)
    val uiState: StateFlow<CreateQRUiState> = _uiState.asStateFlow()

    /**
     * Represents the coroutine job that runs the countdown timer after a QR code is successfully generated.
     *
     * This property is marked with [VisibleForTesting] to allow unit tests to verify that
     * the countdown has started without relying on time-based assertions or testing internal
     * coroutine behavior, which would be fragile and tightly coupled to implementation details.
     *
     * Testing for job existence provides a lightweight and reliable way to assert that the timer
     * logic was triggered, without introducing unnecessary complexity or exposing full timer state.
     *
     * ⚠️ This property must not be accessed or modified from production code.
     */
    @VisibleForTesting
    internal var countdownJob: Job? = null
        private set

    fun generateNewSeed() {
        viewModelScope.launch {
            generateSeedUseCase()
                .onSuccess { seed -> handleSeedSuccess(seed) }
                .onError { error -> handleError(error) }
        }
    }

    private fun handleSeedSuccess(seed: Seed) {
        val image = qrCodeGenerator.generate(seed.value, BITMAP_SIZE)

        if (image == null) {
            _uiState.value = CreateQRUiState.Error(
                stringMapper.mapErrorString(SeedError.InvalidQRGeneration)
            )
            return
        }

        val expiresIn = seed.getSecondsRemaining()

        _uiState.value = CreateQRUiState.Success(
            qrImage = image,
            qrValue = seed.value,
            expiresInSeconds = expiresIn
        )

        startCountdown(expiresIn)
    }

    private fun handleError(error: SeedError) {
        _uiState.value = CreateQRUiState.Error(
            stringMapper.mapErrorString(error)
        )
    }

    private fun startCountdown(duration: Int) {
        countdownJob?.cancel()

        if (duration <= 0) {
            Log.w(TAG, "Countdown not started - expired")
            return
        }

        countdownJob = viewModelScope.launch {
            for (remaining in duration downTo 1) {
                delay(COUNTDOWN_INTERVAL_MS)
                updateRemainingTime(remaining - 1)
            }
        }
    }

    private fun updateRemainingTime(remaining: Int) {
        val currentState = _uiState.value
        if (currentState is CreateQRUiState.Success) {
            _uiState.value = currentState.copy(expiresInSeconds = remaining)
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}

sealed interface CreateQRUiState {
    data object Loading : CreateQRUiState
    data class Error(val message: String) : CreateQRUiState
    data class Success(val qrImage: Bitmap, val qrValue: String, val expiresInSeconds: Int) :
        CreateQRUiState
}
