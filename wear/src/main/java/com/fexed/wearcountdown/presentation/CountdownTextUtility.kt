package com.fexed.wearcountdown.presentation

enum class CountdownType {
    SHORT, LONG_NO_SECONDS, LONG
}

fun countdown(difference: Long, type: CountdownType): String {
    return when (type) {
        CountdownType.LONG -> longCountdown(difference)
        CountdownType.LONG_NO_SECONDS -> noSecondsCountdowun(difference)
        CountdownType.SHORT -> shortCountdown(difference)
    }
}

fun longCountdown(n: Long): String {
    var difference = n
    val days: Long = difference / (24 * 3600)
    difference %= (24 * 3600)
    val hours: Long = difference / 3600
    difference %= 3600
    val minutes: Long = difference / 60
    difference %= 60
    return "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}${if (hours > 0 || minutes > 0) "${minutes}m " else ""}${difference}s"
}

fun noSecondsCountdowun(n: Long): String {
    var difference = n
    val days: Long = difference / (24 * 3600)
    difference %= (24 * 3600)
    val hours: Long = difference / 3600
    difference %= 3600
    val minutes: Long = difference / 60
    difference %= 60
    return if (days == 0L && hours == 0L && minutes == 0L) "${difference}s"
    else "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}${if (hours > 0 || minutes > 0) "${minutes}m" else ""}"
}

fun shortCountdown(n: Long): String {
    var difference = n
    val days: Long = difference / (24 * 3600)
    difference %= (24 * 3600)
    val hours: Long = difference / 3600
    difference %= 3600
    val minutes: Long = difference / 60
    difference %= 60

    var text = ""

    text = if (days > 0) {
        "${days}d ${hours}h"
    } else {
        if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }

    return text
}