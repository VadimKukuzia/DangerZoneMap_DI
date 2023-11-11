package com.veseleil.dangerzonemap_di.data.repository

import com.veseleil.dangerzonemap_di.data.model.Zone
import com.veseleil.dangerzonemap_di.data.remote.ApiService
import com.veseleil.dangerzonemap_di.domain.repository.IZoneRepository
import retrofit2.Response
import javax.inject.Inject

class ZoneRepository @Inject constructor(
    private val api: ApiService
): IZoneRepository {
    override suspend fun addNewZone(accessToken: String, zone: Zone) {
        api.addNewZone(accessToken, zone)
    }

    override suspend fun getZonesToShow(accessToken: String): Response<List<Zone>> {
        return api.getZonesToShow(accessToken)
    }
}