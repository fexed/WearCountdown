package com.fexed.wearcountdown.presentation

sealed class Destinations(val route: String) {
    object MainPage: Destinations("MainPage")
    object TargetDatePicker: Destinations("TargetDatePicker")
    object OriginDatePicker: Destinations("OriginDatePicker")
    object LabelPicker: Destinations("LabelPicker")
    object EditDialog: Destinations("EditDialog")
}