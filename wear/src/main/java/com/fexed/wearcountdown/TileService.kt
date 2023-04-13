package com.fexed.wearcountdown

import androidx.wear.tiles.*
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.material.CircularProgressIndicator
import com.fexed.wearcountdown.presentation.MainActivity
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.SuspendingTileService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalHorologistTilesApi::class)
class TileService : SuspendingTileService()  {
    @ExperimentalHorologistTilesApi
    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .build()
    }

    @ExperimentalHorologistTilesApi
    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout())
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(60 * 1000)
            .build()
    }

    private fun tileLayout(): LayoutElement {
        val prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        val targetDate =
            Instant.parse(prefs.getString(MainActivity.TargetDate_key, null) ?: "1970-01-01T00:00:00.00Z")
        val originDate =
            Instant.parse(prefs.getString(MainActivity.OriginDate_key, null) ?: "1970-01-01T00:00:00.00Z")
        var current = (targetDate.epochSecond - Instant.now().epochSecond)

        val mTimeZone: TimeZone = GregorianCalendar().timeZone
        val mGMTOffset: Int = (mTimeZone.rawOffset + if (mTimeZone.inDaylightTime(Date())) mTimeZone.dstSavings else 0)/1000
        current -= mGMTOffset

        current = current.coerceAtLeast(0)

        val labelFormatter = DateTimeFormatter.ofPattern("yyyy / MM / dd").withZone(ZoneId.systemDefault())
        val dateLabel = prefs.getString(MainActivity.Label_key, null) ?: labelFormatter.format(targetDate)
        val max = (targetDate.epochSecond - originDate.epochSecond).coerceAtLeast(0)
        val perc = current.toFloat() / max

        val progressBar : CircularProgressIndicator = CircularProgressIndicator.Builder().setProgress(perc).build()

        return LayoutElementBuilders.Box.Builder()
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent (
                        LayoutElementBuilders.Text.Builder()
                            .setText(Countdown(current))
                            .setModifiers(
                                ModifiersBuilders.Modifiers.Builder()
                                    .setClickable(launchAppClickable(openApp())).build()
                            )
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(dateLabel)
                            .setModifiers(ModifiersBuilders.Modifiers.Builder().setClickable(launchAppClickable(openApp())).build())
                            .build()
                    )
                    .build()
            )
            .addContent(progressBar)
            .build()
    }

    fun Countdown(n: Long): String {
        var difference = n
        val days: Long = difference / (24 * 3600)
        difference %= (24 * 3600)
        val hours: Long = difference / 3600
        difference %= 3600
        val minutes: Long = difference / 60
        difference %= 60
        return "${if (days > 0) "${days}d " else ""}${if (days > 0 || hours > 0) "${hours}h " else ""}${if (hours > 0 || minutes > 0) "${minutes}m " else ""}"
    }

    internal fun launchAppClickable(
        androidActivity: ActionBuilders.AndroidActivity
    ) = ModifiersBuilders.Clickable.Builder()
        .setOnClick(
            ActionBuilders.LaunchAction.Builder()
                .setAndroidActivity(androidActivity)
                .build()
        )
        .build()

    internal fun openApp() = ActionBuilders.AndroidActivity.Builder()
        .setPackageName("com.fexed.wearcountdown")
        .setClassName("com.fexed.wearcountdown.presentation.MainActivity")
        .build()
}