package com.solara.qrscanner.ui.view

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.solara.qrscanner.R
import com.solara.qrscanner.ui.viewmodel.CreateQRUiState
import com.solara.qrscanner.ui.viewmodel.CreateQRViewModel

@Composable
internal fun CreateQRScreen(
    viewModel: CreateQRViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CreateQRScreenInternal(uiState)

    LaunchedEffect(true) {
        viewModel.generateNewSeed()
    }
}

@Composable
private fun CreateQRScreenInternal(
    uiState: CreateQRUiState,
) {
    when (uiState) {
        is CreateQRUiState.Loading -> {
            LoadingScreen()
        }

        is CreateQRUiState.Error -> {
            ErrorScreen(uiState.message)
        }

        is CreateQRUiState.Success -> {
            SuccessScreen(
                qrImage = uiState.qrImage,
                qrText = uiState.qrValue,
                expiresIn = uiState.expiresInSeconds
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    qrImage: Bitmap,
    qrText: String,
    expiresIn: Int
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                bitmap = qrImage.asImageBitmap(),
                contentDescription = stringResource(R.string.qr_code_description),
                contentScale = ContentScale.Crop,
            )

            Text(
                text = stringResource(R.string.new_qr_seed, qrText),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            ExpirationTimerText(expiresIn = expiresIn)
        }
    }
}

@Composable
fun ExpirationTimerText(expiresIn: Int) {
    val textStyle = MaterialTheme.typography.bodyLarge
    val color = if (expiresIn > 0) LocalContentColor.current else MaterialTheme.colorScheme.error

    Text(
        text = if (expiresIn > 0) {
            stringResource(R.string.expires_in, expiresIn)
        } else {
            stringResource(R.string.expired_qr)
        },
        style = textStyle,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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


@Preview(showBackground = true)
//@MultiDevicePreview
@Composable
fun PreviewCreateQRScreenContentSuccess() {
    CreateQRScreenInternal(
        CreateQRUiState.Success(
            createBitmap(500, 500), "Sample QR Value", 4
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateQRScreenContentError() {
    CreateQRScreenInternal(CreateQRUiState.Error("Error message"))
}


@Preview(showBackground = true)
@Composable
fun PreviewCreateQRScreenContentLoading() {
    CreateQRScreenInternal(CreateQRUiState.Loading)
}
