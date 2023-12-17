package com.veseleil.dangerzonemap_di.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

class GeofenceManager(context: Context): ContextWrapper(context){

    private lateinit var pendingIntent: PendingIntent
    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
    }

    fun getGeofence(id: String, latLng: LatLng, radius: Double, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setCircularRegion(latLng.latitude, latLng.longitude, radius.toFloat())
            .setRequestId(id)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getPendingIntent(): PendingIntent {
        if (this::pendingIntent.isInitialized) {
            return  pendingIntent
        }
        val intent: Intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // Разобраться с интентом
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return  pendingIntent

    }

    fun getErrorString(exception: Exception): String {
        if(exception is ApiException) {
            return when (exception.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                else -> exception.localizedMessage!!
            }
        }
        return exception.localizedMessage!!
    }

}