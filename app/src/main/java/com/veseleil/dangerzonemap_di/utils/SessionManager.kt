package com.veseleil.dangerzonemap_di.utils

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject

class SessionManager @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val ZONE_TYPES = "zone_type"
    }

    fun saveAccessToken(token: String) {
        val editor = prefs.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.apply()
    }

    fun fetchAccessToken(): String? {
        return prefs.getString(ACCESS_TOKEN, null)
    }

    fun deleteAccessToken() {
        val editor = prefs.edit()
        editor.remove(ACCESS_TOKEN)
        editor.apply()
    }

    fun putZoneTypes(zoneTypeSet: MutableSet<String>) {
        val editor = prefs.edit()
        editor.putStringSet(ZONE_TYPES, zoneTypeSet)
        Log.d("LOGTAG", "zonetypeset $zoneTypeSet")
        editor.apply()
    }

    fun fetchZoneTypes(): MutableSet<String>? {
        return prefs.getStringSet(ZONE_TYPES, null)
    }
}