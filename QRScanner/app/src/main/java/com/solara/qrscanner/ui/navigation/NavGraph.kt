package com.solara.qrscanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.solara.qrscanner.ui.navigation.MainPaths.CREATE_QR
import com.solara.qrscanner.ui.navigation.MainPaths.HOME
import com.solara.qrscanner.ui.navigation.MainPaths.SCAN_QR
import com.solara.qrscanner.ui.navigation.MainPaths.START_DESTINATION
import com.solara.qrscanner.ui.view.CreateQRScreen
import com.solara.qrscanner.ui.view.HomeScreen
import com.solara.qrscanner.ui.view.ScanQRScreen


object MainPaths {
    const val SCAN_QR = "scan"
    const val CREATE_QR = "create"
    const val HOME = "home"
    const val START_DESTINATION = HOME
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = START_DESTINATION) {
        composable(HOME) { HomeScreen(navController) }
        composable(SCAN_QR) { ScanQRScreen(viewModel= hiltViewModel()) }
        composable(CREATE_QR) { CreateQRScreen(viewModel= hiltViewModel()) }
    }
}
