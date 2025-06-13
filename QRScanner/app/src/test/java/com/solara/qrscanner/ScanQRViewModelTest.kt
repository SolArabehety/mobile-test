package com.solara.qrscanner

import app.cash.turbine.test
import com.solara.core.utils.Result
import com.solara.domain.model.SeedError
import com.solara.domain.usecases.ValidateSeedUseCase
import com.solara.qrscanner.ui.viewmodel.ScanQRUiState
import com.solara.qrscanner.ui.viewmodel.ScanQRViewModel
import com.solara.qrscanner.ui.viewmodel.StringMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanQRViewModelTest {
    private val validateSeedUseCase: ValidateSeedUseCase = mockk()
    private val stringMapper: StringMapper = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ScanQRViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScanQRViewModel(stringMapper, validateSeedUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Scan`() = runTest {
        assertEquals(ScanQRUiState.Scan, viewModel.uiState.value)
    }

    @Test
    fun `validateQRValue emits Loading then Success when use case succeeds`() = runTest {
        val fakeInput = "qr-seed-valid"
        val fakeResult = true
        coEvery { validateSeedUseCase(fakeInput) } returns Result.Success(fakeResult)

        viewModel.validateQRValue(fakeInput)

        viewModel.uiState.test {
            assertEquals(ScanQRUiState.Scan, awaitItem())
            assertEquals(ScanQRUiState.Loading, awaitItem())
            assertEquals(ScanQRUiState.Success(fakeResult), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `validateQRValue emits Loading then Error when use case fails`() = runTest {
        val fakeInput = "qr-seed-invalid"
        val fakeException = SeedError.Unknown
        val expectedMessage = "invalid QR"
        coEvery { validateSeedUseCase(fakeInput) } returns Result.Error(fakeException)
        every { stringMapper.mapErrorString(fakeException) } returns expectedMessage

        viewModel.validateQRValue(fakeInput)

        viewModel.uiState.test {
            assertEquals(ScanQRUiState.Scan, awaitItem())
            assertEquals(ScanQRUiState.Loading, awaitItem())
            assertEquals(ScanQRUiState.Error(expectedMessage), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
