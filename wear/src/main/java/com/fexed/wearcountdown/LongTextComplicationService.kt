package com.fexed.wearcountdown

import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LongTextComplicationService : SuspendingComplicationDataSourceService()  {
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "Countdown: 1d 12h 30m").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.complication_long_text_desc)).build()
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

        val complicationPendingIntent = ComplicationReceiver.getToggleIntent(this, request.complicationInstanceId)

        return when (request.complicationType) {
            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = "${CountdownLong(current)}\n$dateLabel").build(),
                contentDescription = PlainComplicationText
                    .Builder(text = getString(R.string.complication_long_text_desc)).build()
            )
                .setTapAction(complicationPendingIntent)
                .build()

            else -> null
        }
    }

    fun CountdownLong(n: Long): String {
        var difference = n
        val days: Long = difference / (24 * 3600)
        difference %= (24 * 3600)
        val hours: Long = difference / 3600
        difference %= 3600
        val minutes: Long = difference / 60
        difference %= 60
        return "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}${if (hours > 0 || minutes > 0) "${minutes}m " else ""}"
    }
}