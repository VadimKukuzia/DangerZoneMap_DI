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
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                // Get the geofences that were triggered. A single event can trigger
                // multiple geofences.
                val triggeringGeofences = geofencingEvent.triggeringGeofences

                // Send notification and log the transition details.
                // TODO
                notificationHelper.sendHighPriorityNotification(
                    context.getString(R.string.notification_enter_title),
                    context.getString(R.string.notification_body,
                        triggeringGeofences?.get(0)?.requestId ?: "triggeredGeofenceErrorRequest"
                    ),
                    MainActivity::class.java
                )

            } else {
                // Log the error.
                Log.e(TAG, "Error")
            }
        }
    }
}