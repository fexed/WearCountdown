package com.fexed.wearcountdown

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fexed.wearcountdown.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ComplicationReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val result = goAsync()
        scope.launch {
            try {

            } finally {
                result.finish()
            }
        }
    }

    companion object {
        fun getToggleIntent(
            context: Context,
            complicationId: Int
        ): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)

            return PendingIntent.getBroadcast(
                context,
                complicationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}