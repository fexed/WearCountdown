package com.fexed.wearcountdown

import android.content.ComponentName
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import java.time.Instant

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

        val prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        val targetDate =
            Instant.parse(prefs.getString(kTargetDate, null) ?: "1970-01-01T00:00:00.00Z")
        val current = (targetDate.epochSecond - Instant.now().epochSecond).coerceAtLeast(0)


        val complicationPendingIntent = ComplicationReceiver.getToggleIntent(this, ComponentName(this, javaClass), request.complicationInstanceId)

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = CountdownShort(current)).build(),
                contentDescription = PlainComplicationText
                    .Builder(text = getString(R.string.complication_short_text_desc)).build()
            )
                .setTapAction(complicationPendingIntent)
                .build()

            else -> null
        }
    }

    fun CountdownShort(n: Long): String {
        var difference = n
        val days: Long = difference / (24 * 3600)
        difference %= (24 * 3600)
        val hours: Long = difference / 3600
        difference %= 3600
        return "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else "0h"}"
    }
}