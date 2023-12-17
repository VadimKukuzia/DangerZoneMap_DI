package com.veseleil.dangerzonemap_di.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.ui.main.MainActivity
import com.veseleil.dangerzonemap_di.utils.NotificationHelper

class GeofenceBroadcastReceiver: BroadcastReceiver() {

    init {
        Log.d("LOGTAG", "GeofenceBroadcastReceiver inited")
    }

    private val TAG = "GeofenceBroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            // Get the transition type.
            val geofenceTransition = geofencingEvent.geofenceTransition

            val notificationHelper = NotificationHelper(context)

            // Test that the reported transition was of interest.
            Log.d("LOGTAG", "$geofenceTransition $geofencingEvent")
            when (geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
//                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()
                    notificationHelper.sendHighPriorityNotification(
                        context.getString(R.string.notification_enter_title),
                        context.getString(R.string.notification_body,
                            geofencingEvent.triggeringGeofences?.get(0)?.requestId ?: "request id error"
                        ),
                        MainActivity::class.java
                    )
                }
//            Geofence.GEOFENCE_TRANSITION_DWELL -> {
////                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
//                notificationHelper.sendHighPriorityNotification(
//                    context.getString(R.string.notification_dwell_title),
//                    context.getString(R.string.notification_body,
//                        geofencingEvent.triggeringGeofences[0].requestId),
//                    MainActivity::class.java
//                )
//            }
//            Geofence.GEOFENCE_TRANSITION_EXIT -> {
////                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
//                notificationHelper.sendHighPriorityNotification(
//                    context.getString(R.string.notification_exit_title),
//                    context.getString(R.string.notification_body,
//                        geofencingEvent.triggeringGeofences[0].requestId),
//                    MainActivity::class.java
//                )
//            }
            }
        }
    }
}