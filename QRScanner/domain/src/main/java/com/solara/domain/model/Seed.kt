package com.solara.domain.model

import java.util.Date


/**
 * Model representing a QR seed with expiration.
 *
 * @property value The seed string used to generate QR codes.
 * @property expiredDate The UTC expiration date of the seed.
 */
data class Seed(
    val value: String,
    val expiredDate: Date
){

    /**
     * Returns the number of seconds remaining until expiration.
     * If expired, returns 0.
     */
    fun getSecondsRemaining(): Int {
        val currentTime = System.currentTimeMillis()
        val expirationTime = expiredDate.time
        val diffMillis = expirationTime - currentTime
        val seconds = diffMillis / 1000
        return if (seconds > Int.MAX_VALUE) Int.MAX_VALUE else seconds.coerceAtLeast(0).toInt()
    }

}
