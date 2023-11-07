package com.veseleil.dangerzonemap_di.data.remote

import retrofit2.http.GET

interface ApiService {

    @GET
    suspend fun doNothing() = "Nothing"
}