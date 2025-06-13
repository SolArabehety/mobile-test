package com.solara.data.networking

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(
    private val maxRetries: Int = MAX_RETRIES_COUNT,
    private val delayMs: Long = DELAY_MS
) : Interceptor {

    companion object {
        const val TAG = "RetryInterceptor"
        const val MAX_RETRIES_COUNT = 1
        const val DELAY_MS = 1300L
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var tryCount = 0
        var lastException: IOException? = null

        while (tryCount <= maxRetries) {
            try {
                val response = chain.proceed(chain.request())

                if (response.code in 502..504 && tryCount < maxRetries) {
                    response.close()
                    Thread.sleep(delayMs)
                    tryCount++
                    continue
                }

                return response
            } catch (e: IOException) {
                Log.e(TAG, "retry count: $tryCount - exception catch: ${e.printStackTrace()}")
                lastException = e
                if (tryCount >= maxRetries) throw e
                Thread.sleep(delayMs)
                tryCount++
            }
        }
        throw lastException ?: IOException("Unknown network error")
    }
}
