package com.veseleil.dangerzonemap_di.data.remote

import com.veseleil.dangerzonemap_di.data.model.Zone
import com.veseleil.dangerzonemap_di.data.remote.request.AuthRequest
import com.veseleil.dangerzonemap_di.data.remote.request.GoogleSignInRequest
import com.veseleil.dangerzonemap_di.data.remote.request.RegistrationRequest
import com.veseleil.dangerzonemap_di.data.remote.response.AuthResponse
import com.veseleil.dangerzonemap_di.data.remote.response.RegistrationResponse
import com.veseleil.dangerzonemap_di.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST(Constants.LOGIN_URL)
    suspend fun authorization(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST(Constants.GOOGLE_SIGNIN_URL)
    suspend fun googleSignIn(@Body googleSignInRequest: GoogleSignInRequest): Response<AuthResponse>

    @POST(Constants.REGISTRATION_URL)
    suspend fun registration(@Body registrationRequest: RegistrationRequest): Response<RegistrationResponse>

    @POST(Constants.ADD_NEW_ZONE_URL)
    suspend fun addNewZone(
        @Header("Authorization") accessToken: String,
        @Body newZone: Zone
    ): Response<Zone>

    @GET(Constants.ZONES_TO_SHOW_URL)
    suspend fun getZonesToShow(@Header("Authorization") accessToken: String): Response<List<Zone>>
}