package com.veseleil.dangerzonemap_di.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("tokens")
    val tokens: Tokens
)
