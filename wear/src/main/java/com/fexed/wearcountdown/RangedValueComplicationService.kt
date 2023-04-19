package com.fexed.wearcountdown

import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import java.time.Instant

class RangedValueComplicationService : SuspendingComplicationDataSourceService()  {
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    0.66f,
                    0.0f,
                    1.0f,
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.complication_ranged_value_desc)).build()
                ).setTapAction(null).build()
            }
            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val kTargetDate = getString(R.string.targetDateKey)
        val kOriginDate = getString(R.string.originDateKey)

        val prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        val targetDate =
            Instant.parse(prefs.getString(kTargetDate, null) ?: "1970-01-01T00:00:00.00Z")
        val originDate =
            Instant.parse(prefs.getString(kOriginDate, null) ?: "1970-01-01T00:00:00.00Z")
        val current = (targetDate.epochSecond - Instant.now().epochSecond).coerceAtLeast(0)

        val max = (targetDate.epochSecond - originDate.epochSecond).coerceAtLeast(0)
        val perc = current.toFloat() / max

        val complicationPendingIntent = ComplicationReceiver.getToggleIntent(this, request.complicationInstanceId)

        return when (request.complicationType) {
            ComplicationType.RANGED_VALUE -> RangedValueComplicationData.Builder(
                value = perc,
                min = 0f,
                max = 1.0f,
                contentDescription = PlainComplicationText
                    .Builder(text = getString(R.string.complication_ranged_value_desc)).build()
            )
                .setText(PlainComplicationText.Builder(text = CountdownShort(current)).build())
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
        return "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}"
    }
}