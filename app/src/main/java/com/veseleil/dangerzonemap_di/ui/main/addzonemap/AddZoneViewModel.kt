package com.veseleil.dangerzonemap_di.ui.main.addzonemap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veseleil.dangerzonemap_di.data.model.Zone
import com.veseleil.dangerzonemap_di.data.repository.ZoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddZoneViewModel @Inject constructor(
    private val repository: ZoneRepository
): ViewModel() {
    fun addNewZone(
        accessToken: String,
        name: String,
        zoneType: String,
        latitude: Double,
        longitude: Double,
        radius: Int,
        description: String
    ) {
        val newZone = Zone(name, zoneType, latitude, longitude, radius, description)
        viewModelScope.launch {
            repository.addNewZone(accessToken, newZone)
        }
    }
}