package com.veseleil.dangerzonemap_di.data.repository

import com.veseleil.dangerzonemap_di.data.remote.ApiService
import com.veseleil.dangerzonemap_di.domain.repository.IUserRepository

class UserRepository (
    private val api: ApiService
): IUserRepository {

    override suspend fun doNetworkCall() {
        TODO("Not yet implemented")
    }
}