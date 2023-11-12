package com.veseleil.dangerzonemap_di.data.remote.response

import com.google.gson.annotations.SerializedName
import com.veseleil.dangerzonemap_di.data.model.Tokens

data class AuthResponse(
    @SerializedName("username")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("tokens")
    val tokens: Tokens
)
