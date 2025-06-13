package com.solara.core.utils

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import javax.inject.Inject

/**
 * Generates QR codes from a given string.
 *
 * This class uses ZXing's [QRCodeWriter] to create a [Bitmap] QR code.
 * It can be injected using Hilt.
 */
class QrCodeGenerator @Inject constructor() {

    /**
     * Generates a QR code [Bitmap] from the given [data] and [size].
     *
     * @param data Text to encode in the QR code.
     * @param size Width and height in pixels.
     * @return A [Bitmap] with the QR code, or `null` if an error occurs.
     */
    fun generate(data: String, size: Int): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] =
                        if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                }
            }
            createBitmap(width, height).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            null
        }
    }
}