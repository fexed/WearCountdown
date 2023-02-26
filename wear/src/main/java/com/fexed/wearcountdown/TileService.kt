package com.fexed.wearcountdown

import androidx.wear.tiles.*
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.material.CircularProgressIndicator
import com.fexed.wearcountdown.presentation.MainActivity
import com.google.android.horologist.tiles.ExperimentalHorologistTilesApi
import com.google.android.horologist.tiles.SuspendingTileService
import java.time.Instant

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
        val current = (targetDate.epochSecond - Instant.now().epochSecond).coerceAtLeast(0)
        val max = (targetDate.epochSecond - originDate.epochSecond).coerceAtLeast(0)
        val perc = current.toFloat() / max

        val progressBar : CircularProgressIndicator = CircularProgressIndicator.Builder().setProgress(perc).build()

        return LayoutElementBuilders.Box.Builder()
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(Countdown(current))
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
}