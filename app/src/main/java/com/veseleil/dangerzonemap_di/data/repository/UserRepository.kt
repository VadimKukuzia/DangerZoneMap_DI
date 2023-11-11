package com.veseleil.dangerzonemap_di.data.repository

import android.util.Log
import com.veseleil.dangerzonemap_di.data.remote.ApiService
import com.veseleil.dangerzonemap_di.data.remote.request.AuthRequest
import com.veseleil.dangerzonemap_di.data.remote.request.GoogleSignInRequest
import com.veseleil.dangerzonemap_di.data.remote.request.RegistrationRequest
import com.veseleil.dangerzonemap_di.data.remote.response.AuthResponse
import com.veseleil.dangerzonemap_di.data.remote.response.RegistrationResponse
import com.veseleil.dangerzonemap_di.domain.repository.IUserRepository
import retrofit2.Response

class UserRepository (
    private val api: ApiService
): IUserRepository {

    override suspend fun authorize(authRequest: AuthRequest): Response<AuthResponse> {
        return api.authorization(authRequest)

    }

    override suspend fun googleSignIn(googleSignInRequest: GoogleSignInRequest): Response<AuthResponse> {
        return api.googleSignIn(googleSignInRequest)
    }

    override suspend fun register(registrationRequest: RegistrationRequest): Response<RegistrationResponse> {
        return api.registration(registrationRequest)

    }
}