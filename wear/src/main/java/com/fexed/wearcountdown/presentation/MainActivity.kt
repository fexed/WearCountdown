package com.fexed.wearcountdown.presentation

import android.app.Activity
import android.app.RemoteInput
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.RemoteInputIntentHelper.Companion.putRemoteInputsExtra
import com.fexed.wearcountdown.presentation.theme.WearCountdownTheme
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    companion object {
        const val TargetDate_key = "targetDBG2";
        const val OriginDate_key = "originDBG2";
        const val Label_key = "labelDBG2";
    }

    private lateinit var navController: NavHostController
    private lateinit var prefs: SharedPreferences
    private lateinit var targetDate: Instant
    private lateinit var originDate: Instant
    private lateinit var dateLabel: String
    private val formatter = DateTimeFormatter.ISO_INSTANT
    private val labelFormatter =
        DateTimeFormatter.ofPattern("yyyy / MM / dd").withZone(ZoneId.systemDefault())
    private val tz = ZoneId.systemDefault()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var tempLabel: MutableState<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        targetDate =
            Instant.parse(prefs.getString(TargetDate_key, null) ?: "1970-01-01T00:00:00.00Z")
        originDate =
            Instant.parse(prefs.getString(OriginDate_key, null) ?: "1970-01-01T00:00:00.00Z")
        dateLabel = prefs.getString(Label_key, null) ?: labelFormatter.format(targetDate)

        if (ZonedDateTime.ofInstant(originDate, tz).year == 1970) {
            originDate = Instant.now()
        }

        if (ZonedDateTime.ofInstant(targetDate, tz).year == 1970) {
            targetDate = Instant.now().plusMillis(10000)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val results = RemoteInput.getResultsFromIntent(result.data)
                if (results != null) {
                    tempLabel.value = results.getCharSequence("input_label").toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setContent {
            navController = rememberSwipeDismissableNavController()
            var difference = targetDate.epochSecond - originDate.epochSecond

            var tempOrigin by remember { mutableStateOf(originDate) }
            var tempTarget by remember { mutableStateOf(targetDate) }
            tempLabel = remember { mutableStateOf(dateLabel) }

            Log.d("TARGET", labelFormatter.format(tempTarget))
            Log.d("ORIGIN", labelFormatter.format(tempOrigin))
            Log.d("LABEL", dateLabel)

            SwipeDismissableNavHost(
                navController = navController, startDestination = Destinations.MainPage.route
            ) {
                composable(route = Destinations.MainPage.route) {
                    MainProgressPage(targetDate, difference, dateLabel) {
                        navController.navigate(Destinations.EditDialog.route)
                    }
                }

                composable(route = Destinations.TargetDatePicker.route) {
                    tempTarget = targetDate
                    DatePickerDialog(currentDate = targetDate.atZone(tz).toLocalDate()) {
                        tempTarget = it!!.atStartOfDay(tz).toInstant()

                        navController.popBackStack()
                    }
                }

                composable(route = Destinations.OriginDatePicker.route) {
                    tempOrigin = originDate
                    DatePickerDialog(
                        currentDate = originDate.atZone(tz).toLocalDate(), startingDate = null
                    ) {
                        tempOrigin = it!!.atStartOfDay(tz).toInstant()
                        navController.popBackStack()
                    }
                }

                composable(route = Destinations.LabelPicker.route) {
                    tempLabel.value = labelFormatter.format(tempTarget)
                    LabelPickerDialog(label = tempLabel.value) {
                        tempLabel.value = it
                        navController.popBackStack()
                    }
                }

                composable(route = Destinations.EditDialog.route) {
                    val settingsLabelDateFormatter = DateTimeFormatter.ofPattern("yyyy / MM / dd")
                        .withZone(ZoneId.systemDefault())

                    SettingsDialog(
                        originDate = settingsLabelDateFormatter.format(tempOrigin),
                        originDateEdit = {
                            navController.navigate(Destinations.OriginDatePicker.route)
                        },
                        targetDate = settingsLabelDateFormatter.format(tempTarget),
                        targetDateEdit = {
                            navController.navigate(Destinations.TargetDatePicker.route)
                        },
                        label = tempLabel.value,
                        labelEdit = {
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder("input_label").setLabel("Countdown label").build()
                            )
                            val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                            putRemoteInputsExtra(intent, remoteInputs)
                            resultLauncher.launch(intent)
                        },
                        confirmBtn = {
                            originDate = tempOrigin
                            targetDate = tempTarget
                            dateLabel = tempLabel.value

                            prefs.edit().putString(OriginDate_key, formatter.format(originDate))
                                .apply()
                            prefs.edit().putString(TargetDate_key, formatter.format(targetDate))
                                .apply()
                            prefs.edit().putString(Label_key, dateLabel)
                                .apply()
                            difference = targetDate.epochSecond - originDate.epochSecond
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainProgressPage(
    targetDate: Instant, max: Long, label: String, editDialogNavigation: (() -> Unit)
) {
    var now by remember { mutableStateOf(Instant.now()) }
    val current = (targetDate.epochSecond - now.epochSecond).coerceAtLeast(0)
    val perc = current.toFloat() / max

    if (current > 0) {
        Handler(Looper.getMainLooper()).postDelayed({
            now = Instant.now()
        }, 1000)
    }

    WearCountdownTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    editDialogNavigation.invoke()
                }, contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                progress = perc,
                trackColor = Color.Transparent
            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
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