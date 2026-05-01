package com.example.tailorflow.navigation

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Form : Screen("form")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Auth : Screen("auth")
    object ShopSetup : Screen("shop_setup")
}