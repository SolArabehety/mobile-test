package com.solara.data

import com.solara.data.networking.ApiService
import com.solara.data.networking.requests.ValidateSeedRequest
import com.solara.data.networking.responses.SeedResponse
import com.solara.data.networking.responses.ValidateSeedResponse
import com.solara.data.repositories.QRRepositoryImpl
import com.solara.domain.repositories.ConnectionErrorException
import com.solara.domain.repositories.SeedServerException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class QRRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private lateinit var repository: QRRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = QRRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getNewSeed returns Seed on success`() = runTest {
        val date = Date()
        val dto = SeedResponse("abc123", date)
        val expected = dto.toModel()

        coEvery { apiService.getNewSeed() } returns dto

        val result = repository.getNewSeed()

        assertEquals(expected, result)
    }

    @Test(expected = ConnectionErrorException::class)
    fun `getNewSeed throws ConnectionErrorException on IOException`() = runTest {
        coEvery { apiService.getNewSeed() } throws IOException("Timeout")

        repository.getNewSeed()
    }

    @Test(expected = SeedServerException::class)
    fun `getNewSeed throws SeedServerException on HttpException`() = runTest {
        val httpException = mockk<HttpException>()
        coEvery { apiService.getNewSeed() } throws httpException

        repository.getNewSeed()
    }

    @Test
    fun `validateSeed returns true when valid`() = runTest {
        val value = "abc123"
        val response = ValidateSeedResponse(valid = true)

        coEvery { apiService.validateSeed(ValidateSeedRequest(value)) } returns response

        val result = repository.validateSeed(value)

        assertTrue(result)
    }

    @Test
    fun `validateSeed returns false when invalid`() = runTest {
        val value = "invalid123"
        val response = ValidateSeedResponse(valid = false)

        coEvery { apiService.validateSeed(ValidateSeedRequest(value)) } returns response

        val result = repository.validateSeed(value)

        assertFalse(result)
    }

    @Test(expected = ConnectionErrorException::class)
    fun `validateSeed throws ConnectionErrorException on IOException`() = runTest {
        val value = "abc123"
        coEvery { apiService.validateSeed(any()) } throws IOException("Network down")

        repository.validateSeed(value)
    }

    @Test(expected = SeedServerException::class)
    fun `validateSeed throws SeedServerException on HttpException`() = runTest {
        val value = "abc123"

        val response = Response.error<Unit>(
            500,
            "Server error".toResponseBody("application/json".toMediaTypeOrNull())
        )

        val httpException = HttpException(response)

        coEvery { apiService.validateSeed(any()) } throws httpException

        repository.validateSeed(value)
    }
}
