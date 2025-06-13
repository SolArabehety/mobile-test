package com.solara.core.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ALL_FORMATS
import com.google.mlkit.vision.common.InputImage

/**
 * QRCodeAnalyzer is an implementation of [ImageAnalysis.Analyzer] that uses ML Kit's
 * BarcodeScanning API to detect QR codes or other barcode formats in camera frames.
 *
 * @property onBarcodeDetected Callback invoked with the decoded string value when a barcode is detected.
 *
 * This analyzer processes frames using the default ML Kit barcode scanner and supports all barcode formats.
 * It safely handles image closing and logs errors when image processing fails.
 *
 * Usage:
 * ```
 * val analyzer = QRCodeAnalyzer { qrValue ->
 *     // Handle the detected QR value
 * }
 * ```
 */
class QRCodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "QRCodeAnalyzer"

    }

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { value ->
                    Log.d(TAG, "Detected value: $value")
                    onBarcodeDetected(value)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error processing the image", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}