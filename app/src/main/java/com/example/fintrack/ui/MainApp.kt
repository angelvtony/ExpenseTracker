package com.example.fintrack.ui


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fintrack.ui.screen.MainScreen
import com.example.fintrack.ui.screen.support.OnBoardingScreen
import com.example.fintrack.ui.util.MainViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel) {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = mainViewModel.screenDestination.value ){
        composable(route="mainScreen"){ MainScreen(viewModel = mainViewModel) }
        composable(route="onBoardingScreen"){ OnBoardingScreen(rootNavController,mainViewModel) }
        composable(route="blankScreen"){}
    }
}
