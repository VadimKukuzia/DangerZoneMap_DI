package com.veseleil.dangerzonemap_di.data.remote.request

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    private val email: String,
    @SerializedName("password")
    private val password: String
)
