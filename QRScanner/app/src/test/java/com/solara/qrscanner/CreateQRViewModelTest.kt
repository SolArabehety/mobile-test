package com.solara.qrscanner


import com.solara.core.utils.QrCodeGenerator
import com.solara.core.utils.Result
import com.solara.domain.model.Seed
import com.solara.domain.model.SeedError
import com.solara.domain.usecases.GenerateSeedUseCase
import com.solara.qrscanner.ui.viewmodel.CreateQRUiState
import com.solara.qrscanner.ui.viewmodel.CreateQRViewModel
import com.solara.qrscanner.ui.viewmodel.StringMapper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CreateQRViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: CreateQRViewModel

    private val stringMapper = mockk<StringMapper>(relaxed = true)
    private val qrCodeGenerator = mockk<QrCodeGenerator>(relaxed = true)
    private val generateSeedUseCase = mockk<GenerateSeedUseCase>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateQRViewModel(stringMapper, qrCodeGenerator, generateSeedUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `generateNewSeed emits Success when seed and QR are valid`() = runTest {
        val seed = Seed(
            value = "123456",
            expiredDate = Date(System.currentTimeMillis() + 5000)
        )

        coEvery { generateSeedUseCase() } returns Result.Success(seed)

        viewModel.generateNewSeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CreateQRUiState.Success)

        val success = state as CreateQRUiState.Success
        assertEquals("123456", success.qrValue)
        assertNotNull(success.qrImage)
    }

    @Test
    fun `generateNewSeed emits Error when QR generation fails`() = runTest {
        val seed = Seed(
            value = "test",
            expiredDate = Date(System.currentTimeMillis() + 5000)
        )

        coEvery { generateSeedUseCase() } returns Result.Success(seed)
        every { qrCodeGenerator.generate(any(), any()) } returns null
        every { stringMapper.mapErrorString(SeedError.InvalidQRGeneration) } returns "QR inválido"

        viewModel.generateNewSeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CreateQRUiState.Error)
        assertEquals("QR inválido", (state as CreateQRUiState.Error).message)
    }

    @Test
    fun `generateNewSeed emits Error when use case fails`() = runTest {
        val error = SeedError.ServerError
        coEvery { generateSeedUseCase() } returns Result.Error(error)
        every { stringMapper.mapErrorString(error) } returns "Error de red"

        viewModel.generateNewSeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is CreateQRUiState.Error)
        assertEquals("Error de red", (state as CreateQRUiState.Error).message)
    }

    @Test
    fun `countdownJob is started when seed is valid`() = runTest {
        val seed = Seed("abc123", Date(System.currentTimeMillis() + 5000))

        coEvery { generateSeedUseCase() } returns Result.Success(seed)
        every { qrCodeGenerator.generate(any(), any()) } returns mockk()

        viewModel.generateNewSeed()
        advanceUntilIdle()

        assertNotNull("Countdown should be started", viewModel.countdownJob)
    }


}

