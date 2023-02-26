package com.fexed.wearcountdown.presentation

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.fexed.wearcountdown.presentation.theme.WearCountdownTheme
import com.google.android.horologist.composables.DatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    companion object {
        const val TargetDate_key = "targetDBG1";
        const val OriginDate_key = "originDBG1";
        const val Label_key = "labelDBG1";
    }

    private lateinit var navController: NavHostController
    private lateinit var prefs: SharedPreferences
    private lateinit var targetDate : Instant
    private lateinit var originDate : Instant
    private lateinit var dateLabel : String
    private val formatter = DateTimeFormatter.ISO_INSTANT
    private val labelFormatter = DateTimeFormatter.ISO_DATE
    private val tz = ZoneId.systemDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getPreferences(MODE_PRIVATE)
        targetDate = Instant.parse(prefs.getString(TargetDate_key, "1970-01-01T00:00:00.00Z"))
        originDate = Instant.parse(prefs.getString(OriginDate_key, "1970-01-01T00:00:00.00Z"))
        dateLabel = "${targetDate.atZone(tz).year}-${targetDate.atZone(tz).month}-${targetDate.atZone(tz).dayOfMonth}"

        if (ZonedDateTime.ofInstant(targetDate, tz).year == 1970) {
            targetDate = Instant.now().plusMillis(10000)
        }

        if (ZonedDateTime.ofInstant(originDate, tz).year == 1970) {
            originDate = Instant.now()
            //prefs.edit().putString("originDate", formatter.format(originDate)).apply()
        }
    }

    override fun onResume() {
        super.onResume()

        setContent {
            navController = rememberSwipeDismissableNavController()
            var difference = targetDate.epochSecond - originDate.epochSecond

            SwipeDismissableNavHost(navController = navController, startDestination = Destinations.MainPage.route) {
                composable(route = Destinations.MainPage.route) {
                    MainProgressPage(targetDate, difference, dateLabel) {
                        navController.navigate(Destinations.EditDialog.route)
                    }
                }

                composable(route = Destinations.EditDialog.route) {
                    EditDialog(currentDate = targetDate.atZone(tz).toLocalDate()) {
                        originDate = Instant.now()
                        targetDate = it!!.atStartOfDay(tz).toInstant()
                        dateLabel = "${targetDate.atZone(tz).year}-${targetDate.atZone(tz).month}-${targetDate.atZone(tz).dayOfMonth}"
                        prefs.edit().putString(OriginDate_key, formatter.format(Instant.now())).apply()
                        prefs.edit().putString(TargetDate_key, formatter.format(targetDate)).apply()
                        difference = targetDate.epochSecond - originDate.epochSecond
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
fun MainProgressPage(targetDate: Instant, max: Long, label: String, editDialogNavigation : (() -> Unit)) {
    var now by remember { mutableStateOf(Instant.now()) }
    val current = (targetDate.epochSecond - now.epochSecond).coerceAtLeast(0)
    val perc = current.toFloat() / max

    if (current > 0) {
        Handler(Looper.getMainLooper()).postDelayed({
            now = Instant.now()
        }, 1000)
    }

    WearCountdownTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                editDialogNavigation.invoke()
            }, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize(), strokeWidth = 10.dp, progress = perc, trackColor = Color.Transparent)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Countdown(n = current)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp,
                    text = label
                )
            }
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings button")
            }
        }
    }
}

@Composable
fun Countdown(n: Long) {
    var difference = n
    val days: Long = difference / (24 * 3600)
    difference %= (24 * 3600)
    val hours: Long = difference / 3600
    difference %= 3600
    val minutes: Long = difference / 60
    difference %= 60
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 18.sp,
        text = "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}${if (hours > 0 || minutes > 0) "${minutes}m " else ""}${difference}s"
    )
}

@Composable
fun EditDialog(currentDate : LocalDate?, onDismiss : ((LocalDate?) -> Unit)) {
    val startingDate = LocalDate.now()
    WearCountdownTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 18.sp,
                    text = "Set target date"
                )

                DatePicker(onDateConfirm = {
                    onDismiss(it)
                }, fromDate = startingDate, date = currentDate ?: startingDate)
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun RoundPreview() {
    val targetDate = Instant.parse("2023-02-27T00:00:00.00Z")
    val originDate = Instant.parse("2023-02-15T00:00:00.00Z")
    val difference = targetDate.epochSecond - originDate.epochSecond
    MainProgressPage(targetDate = targetDate, max = difference, "Round $difference") {}
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun SquarePreview() {
    val targetDate = Instant.parse("2023-02-26T16:23:00.00Z")
    val originDate = Instant.parse("2023-02-25T00:00:00.00Z")
    val difference = targetDate.epochSecond - originDate.epochSecond
    MainProgressPage(targetDate = targetDate, max = difference, "Square $difference") {}
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RoundDialogPreview() {
    EditDialog(null) {}
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun SquareDialogPreview() {
    EditDialog(null) {}
}