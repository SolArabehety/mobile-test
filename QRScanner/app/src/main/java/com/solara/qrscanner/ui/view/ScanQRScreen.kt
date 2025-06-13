package com.solara.qrscanner.ui.view

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.solara.core.utils.QRCodeAnalyzer
import com.solara.qrscanner.R
import com.solara.qrscanner.ui.viewmodel.CreateQRUiState
import com.solara.qrscanner.ui.viewmodel.CreateQRViewModel
import com.solara.qrscanner.ui.viewmodel.ScanQRUiState
import com.solara.qrscanner.ui.viewmodel.ScanQRViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun ScanQRScreen(
    viewModel: ScanQRViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !cameraPermissionState.status.isGranted -> {
                PermissionMessage(permissionState = cameraPermissionState)
            }

            else -> {
                AnimatedContent(
                    targetState = uiState,
                ) { state ->
                    when (state) {
                        is ScanQRUiState.Scan -> {
                            CameraView(onBarcodeScanned = { scannedValue ->
                                viewModel.validateQRValue(scannedValue)
                            })
                        }

                        is ScanQRUiState.Loading -> {
                            LoadingScreen()
                        }

                        is ScanQRUiState.Error -> {
                            ErrorScreen(message = state.message)
                        }

                        is ScanQRUiState.Success -> {
                            SuccessMessage(isValid = state.isValid)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionMessage(permissionState: PermissionState) {
    val message = if (permissionState.status.shouldShowRationale) {
        stringResource(R.string.permission_denied)
    } else {
        stringResource(R.string.permission_requesting)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
private fun CameraView(onBarcodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val preview = androidx.camera.core.Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(
                        ContextCompat.getMainExecutor(ctx),
                        QRCodeAnalyzer { barcodeValue ->
                            onBarcodeScanned(barcodeValue)
                        }
                    )
                }

            runCatching {
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            }.onFailure {
                Log.e("CameraView", "Camera initialization error: ${it.localizedMessage}", it)
            }

            previewView
        }
    )
}


@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.il_error),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .aspectRatio(1f)
            )
            Text(
                text = stringResource(R.string.error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun SuccessMessage(isValid: Boolean) {
    val backgroundColor = if (isValid) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = if (isValid) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    val icon = if (isValid) Icons.Default.CheckCircle else Icons.Default.Clear
    val message = if (isValid) {
        stringResource(R.string.qr_valid)
    } else {
        stringResource(R.string.qr_invalid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(top = 32.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewScanQRSuccess() {
    SuccessMessage(isValid = true)
}

@Preview(showBackground = true)
@Composable
fun PreviewScanQRSuccessFalse() {
    SuccessMessage(isValid = false)
}