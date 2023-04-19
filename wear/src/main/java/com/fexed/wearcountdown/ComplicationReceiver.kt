package com.fexed.wearcountdown

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester.Companion.EXTRA_COMPLICATION_IDS
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester.Companion.EXTRA_PROVIDER_COMPONENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ComplicationReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val dataSource = extras.getParcelable<ComponentName>(EXTRA_PROVIDER_COMPONENT) ?: return
        val complicationId = extras.getInt(EXTRA_COMPLICATION_IDS)
        val result = goAsync()
        scope.launch {
            try {
                ComplicationDataSourceUpdateRequester.create(context, dataSource).requestUpdate(complicationId)
            } finally {
                result.finish()
            }
        }
    }

    companion object {
        fun getToggleIntent(
            context: Context,
            dataSource: ComponentName,
            complicationId: Int
        ): PendingIntent {
            val intent = Intent(context, ComplicationReceiver::class.java)
            intent.putExtra(EXTRA_PROVIDER_COMPONENT, dataSource)
            intent.putExtra(EXTRA_COMPLICATION_IDS, complicationId)

            return PendingIntent.getBroadcast(
                context,
                complicationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}