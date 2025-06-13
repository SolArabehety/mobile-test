package com.solara.qrscanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solara.core.onError
import com.solara.core.onSuccess
import com.solara.domain.usecases.ValidateSeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
internal class ScanQRViewModel @Inject constructor(
    private val stringMapper: StringMapper,
    private val validateSeedUseCase: ValidateSeedUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScanQRUiState>(ScanQRUiState.Scan)
    val uiState: StateFlow<ScanQRUiState> = _uiState.asStateFlow()

    fun validateQRValue(value: String) {
        viewModelScope.launch {
            _uiState.value = ScanQRUiState.Loading

            validateSeedUseCase.invoke(value).onSuccess {
                _uiState.value = ScanQRUiState.Success(it)
            }.onError {
                _uiState.value = ScanQRUiState.Error(stringMapper.mapErrorString(it))
            }
        }
    }

}

sealed interface ScanQRUiState {
    data object Scan : ScanQRUiState
    data object Loading : ScanQRUiState
    data class Error(val message: String) : ScanQRUiState
    data class Success(val isValid: Boolean) : ScanQRUiState
}
