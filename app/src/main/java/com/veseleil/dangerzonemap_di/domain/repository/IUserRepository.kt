package com.veseleil.dangerzonemap_di.domain.repository

import com.veseleil.dangerzonemap_di.data.remote.request.AuthRequest
import com.veseleil.dangerzonemap_di.data.remote.request.GoogleSignInRequest
import com.veseleil.dangerzonemap_di.data.remote.request.RegistrationRequest
import com.veseleil.dangerzonemap_di.data.remote.response.AuthResponse
import com.veseleil.dangerzonemap_di.data.remote.response.RegistrationResponse
import retrofit2.Response

interface IUserRepository {

    suspend fun authorize(authRequest: AuthRequest): Response<AuthResponse>
    suspend fun googleSignIn(googleSignInRequest: GoogleSignInRequest): Response<AuthResponse>
    suspend fun register(registrationRequest: RegistrationRequest): Response<RegistrationResponse>

}