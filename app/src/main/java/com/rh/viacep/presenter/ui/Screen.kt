package com.rh.viacep.presenter.ui

sealed class Screen(val route:String){
    object HomeScreen : Screen("home_screen")
    object ResultScreen : Screen("result_screen")
}

