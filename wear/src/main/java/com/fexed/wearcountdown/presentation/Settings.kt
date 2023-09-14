package com.fexed.wearcountdown.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import com.fexed.wearcountdown.R
import com.fexed.wearcountdown.presentation.theme.WearCountdownTheme
import com.google.android.horologist.composables.DatePicker
import java.time.LocalDate


@Composable
fun DatePickerDialog(
    currentDate: LocalDate?,
    startingDate: LocalDate? = LocalDate.now(),
    onDismiss: ((LocalDate?) -> Unit)
) {
    WearCountdownTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DatePicker(onDateConfirm = {
                    onDismiss(it)
                }, fromDate = startingDate, date = currentDate ?: (startingDate ?: LocalDate.now()))
            }
        }
    }
}

@Composable
fun LabelPickerDialog(label: String, confirmBtn : ((String) -> Unit)) {
    var tmp by remember { mutableStateOf(label) }
    WearCountdownTheme {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            BasicTextField(value = tmp, onValueChange = {tmp = it} )
            Button(onClick = { confirmBtn.invoke(tmp) }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    }
}

@Composable
fun SettingsDialog(
    originDate: String,
    targetDate: String,
    label: String,
    originDateEdit: (() -> Unit),
    targetDateEdit: (() -> Unit),
    labelEdit: (() -> Unit),
    confirmBtn: (() -> Unit)
) {
    val listState = rememberScalingLazyListState()
    WearCountdownTheme {
        Row {
            ScalingLazyColumn(
                modifier = Modifier
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                state = listState
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 14.sp,
                            text = stringResource(id = R.string.origin)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 16.sp,
                            text = originDate
                        )
                        Box(modifier = Modifier.clickable { originDateEdit.invoke() }) {
                            Icon(Icons.Filled.Edit, contentDescription = "")
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 14.sp,
                            text = stringResource(id = R.string.target)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 16.sp,
                            text = targetDate
                        )
                        Box(modifier = Modifier.clickable { targetDateEdit.invoke() }) {
                            Icon(Icons.Filled.Edit, contentDescription = "")
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 14.sp,
                            text = stringResource(id = R.string.label)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 12.sp,
                            text = label
                        )
                        Box(modifier = Modifier.clickable { labelEdit.invoke() }) {
                            Icon(Icons.Filled.Edit, contentDescription = "")
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { confirmBtn.invoke() }) {
                            Image(painterResource(id = R.drawable.confirmbtn), contentDescription = "")
                        }
                    }
                }
            }
            PositionIndicator(
                scalingLazyListState = listState
            )
        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, locale = "it")
@Composable
fun RoundDialogPreview() {
    SettingsDialog(originDate = "dd/mm/yyyy", targetDate = "dd/mm/yyyy", label = "label",
        originDateEdit = {}, targetDateEdit = {}, labelEdit = {}, confirmBtn = {})
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun SquareDialogPreview() {
    SettingsDialog(originDate = "dd/mm/yyyy", targetDate = "dd/mm/yyyy", label = "label",
        originDateEdit = {}, targetDateEdit = {}, labelEdit = {}, confirmBtn = {})
}