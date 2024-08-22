package com.example.mobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobileapp.ui.theme.screens.login.LoginScreen
import com.example.mobileapp.ui.theme.screens.register.Greeting

@Composable
fun AppNavHost(
    navController: NavHostController= rememberNavController(),
    startDestination: String= ROUTE_REGISTER
){
    NavHost(navController = navController, startDestination=startDestination){
        composable(ROUTE_REGISTER){ Greeting(navController)}
        composable(ROUTE_LOGIN){ LoginScreen(navController)}
    }


}