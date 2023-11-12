package com.veseleil.dangerzonemap_di.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veseleil.dangerzonemap_di.data.remote.request.AuthRequest
import com.veseleil.dangerzonemap_di.data.remote.response.AuthResponse
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _authResponseLiveData = MutableLiveData<Response<AuthResponse>>()
    val authResponseLiveData get() = _authResponseLiveData

    fun authorize(email: String, password: String) {
        viewModelScope.launch {
            val response = repository.authorize(AuthRequest(email, password))
            _authResponseLiveData.postValue(response)
        }
    }
}