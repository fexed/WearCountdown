package com.fexed.wearcountdown

import android.content.ComponentName
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.fexed.wearcountdown.presentation.CountdownType
import com.fexed.wearcountdown.presentation.countdown
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ShortTextComplicationService : SuspendingComplicationDataSourceService()  {
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "1d 12h").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.complication_short_text_desc)).build()
                ).setTapAction(null).build()
            }
            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val kTargetDate = getString(R.string.targetDateKey)
        val kLabel = getString(R.string.labelKey)

        val prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        val targetDate =
            Instant.parse(prefs.getString(kTargetDate, null) ?: "1970-01-01T00:00:00.00Z")
        val labelFormatter = DateTimeFormatter.ofPattern("yyyy / MM / dd").withZone(ZoneId.systemDefault())
        val dateLabel = prefs.getString(kLabel, null) ?: labelFormatter.format(targetDate)
        val current = (targetDate.epochSecond - Instant.now().epochSecond).coerceAtLeast(0)

        val complicationPendingIntent = ComplicationReceiver.getToggleIntent(this, ComponentName(this, javaClass), request.complicationInstanceId)

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = countdown(current, CountdownType.SHORT)).build(),
                contentDescription = PlainComplicationText
                    .Builder(text = getString(R.string.complication_short_text_desc)).build()
            )
            .setTitle(PlainComplicationText.Builder(text = dateLabel).build())
            .setTapAction(complicationPendingIntent)
            .build()

            else -> null
        }
    }
}