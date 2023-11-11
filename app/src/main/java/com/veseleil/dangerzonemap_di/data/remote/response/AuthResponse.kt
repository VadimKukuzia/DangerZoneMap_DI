package com.veseleil.dangerzonemap_di.data.remote.response

import com.google.gson.annotations.SerializedName
import com.veseleil.dangerzonemap_di.data.model.Tokens

data class AuthResponse(
    @SerializedName("username")
    private val userName: String,
    @SerializedName("email")
    private val email: String,
    @SerializedName("tokens")
    private val tokens: Tokens
)
