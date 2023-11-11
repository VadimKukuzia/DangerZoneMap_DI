package com.veseleil.dangerzonemap_di.data.remote.request

import com.google.gson.annotations.SerializedName

data class GoogleSignInRequest(
    @SerializedName("auth_token")
    private val authToken: String
)
