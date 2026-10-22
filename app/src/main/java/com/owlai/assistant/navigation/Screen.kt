package com.owlai.assistant.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object Chat : Screen("chat")
    data object MathSolver : Screen("math_solver")
    data object PhotoMath : Screen("photo_math")
    data object ImageCreator : Screen("image_creator")
    data object VideoCreator : Screen("video_creator")
    data object Profile : Screen("profile")
    data object Admin : Screen("admin")
}
