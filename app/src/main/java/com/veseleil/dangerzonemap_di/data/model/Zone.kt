package com.veseleil.dangerzonemap_di.data.model

import com.google.gson.annotations.SerializedName

data class Zone(
    @SerializedName("name")
    val name: String,
    @SerializedName("zone_type")
    val zoneType: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("radius")
    val radius: Int,
    @SerializedName("description")
    val description: String
)
