package com.veseleil.dangerzonemap_di.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String
)
