package com.veseleil.dangerzonemap_di.ui.auth.startscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veseleil.dangerzonemap_di.data.remote.request.GoogleSignInRequest
import com.veseleil.dangerzonemap_di.data.remote.response.AuthResponse
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {


    private val _authResponseLiveData = MutableLiveData<Response<AuthResponse>>()

    val authResponseLiveData get() = _authResponseLiveData

    fun signInGoogle(authToken: String) {
        viewModelScope.launch {
            val response = repository.googleSignIn(GoogleSignInRequest(authToken))
            _authResponseLiveData.postValue(response)
        }
    }
}