package com.fexed.wearcountdown.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.Icon
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
            Box(modifier = Modifier
                .wrapContentSize()
                .clickable { confirmBtn.invoke(tmp) }) {
                Image(painterResource(id = R.drawable.confirmbtn), contentDescription = "")
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
    WearCountdownTheme {
        ScalingLazyColumn(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
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
                        text = "Origin"
                    )
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 16.sp,
                        text = originDate
                    )
                    Box(modifier = Modifier.clickable { originDateEdit.invoke() }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit origin date button")
                    }
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
                        text = "Target"
                    )
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 16.sp,
                        text = targetDate
                    )
                    Box(modifier = Modifier.clickable { targetDateEdit.invoke() }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit target date button")
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    /*Text(
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 16.sp,
                        text = "Label"
                    )*/
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 12.sp,
                        text = label
                    )
                    /*Box(modifier = Modifier.clickable { labelEdit.invoke() }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit label button")
                    }*/
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .clickable { confirmBtn.invoke() }) {
                        Image(painterResource(id = R.drawable.confirmbtn), contentDescription = "")
                    }
                }
            }
        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RoundDialogPreview() {
    SettingsDialog(originDate = "origin", targetDate = "target", label = "label",
        originDateEdit = {}, targetDateEdit = {}, labelEdit = {}, confirmBtn = {})
}

@Preview(device = Devices.WEAR_OS_SQUARE, showSystemUi = true)
@Composable
fun SquareDialogPreview() {
    SettingsDialog(originDate = "origin", targetDate = "target", label = "label",
        originDateEdit = {}, targetDateEdit = {}, labelEdit = {}, confirmBtn = {})
}