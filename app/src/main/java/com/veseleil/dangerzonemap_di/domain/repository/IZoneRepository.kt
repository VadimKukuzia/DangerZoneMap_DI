package com.veseleil.dangerzonemap_di.domain.repository

import com.veseleil.dangerzonemap_di.data.model.Zone
import retrofit2.Response

interface IZoneRepository {

    suspend fun addNewZone(accessToken: String, zone: Zone)
    suspend fun getZonesToShow(accessToken: String): Response<List<Zone>>
}