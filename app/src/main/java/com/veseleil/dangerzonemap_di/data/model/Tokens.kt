package com.veseleil.dangerzonemap_di.data.model

import com.google.gson.annotations.SerializedName

data class Tokens(
    @SerializedName("refresh")
    val refreshToken: String,
    @SerializedName("access")
    val accessToken: String
)
