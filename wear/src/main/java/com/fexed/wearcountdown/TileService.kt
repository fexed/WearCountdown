package com.fexed.wearcountdown

import androidx.wear.tiles.*
import androidx.wear.tiles.LayoutElementBuilders.LayoutElement
import androidx.wear.tiles.material.CircularProgressIndicator
import com.fexed.wearcountdown.presentation.CountdownType
import com.fexed.wearcountdown.presentation.MainActivity
import com.fexed.wearcountdown.presentation.countdown
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
        val kTargetDate = getString(R.string.targetDateKey)
        val kOriginDate = getString(R.string.originDateKey)
        val kLabel = getString(R.string.labelKey)

        val prefs = applicationContext.getSharedPreferences("com.fexed.wearcountdown", MODE_PRIVATE)
        val targetDate =
            Instant.parse(prefs.getString(kTargetDate, null) ?: "1970-01-01T00:00:00.00Z")
        val originDate =
            Instant.parse(prefs.getString(kOriginDate, null) ?: "1970-01-01T00:00:00.00Z")
        val current = (targetDate.epochSecond - Instant.now().epochSecond).coerceAtLeast(0)

        val labelFormatter = DateTimeFormatter.ofPattern("yyyy / MM / dd").withZone(ZoneId.systemDefault())
        val dateLabel = prefs.getString(kLabel, null) ?: labelFormatter.format(targetDate)
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
                            .setText(countdown(current, CountdownType.LONG_NO_SECONDS))
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