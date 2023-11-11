package com.veseleil.dangerzonemap_di.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("username")
    private val userName: String,
    @SerializedName("email")
    private val email: String,
    @SerializedName("password")
    private val password: String
)
