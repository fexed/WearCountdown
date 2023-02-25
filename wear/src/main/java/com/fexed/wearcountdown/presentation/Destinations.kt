package com.fexed.wearcountdown.presentation

sealed class Destinations(val route: String) {
    object MainPage: Destinations("MainPage")
    object EditDialog: Destinations("EditDialog")
}