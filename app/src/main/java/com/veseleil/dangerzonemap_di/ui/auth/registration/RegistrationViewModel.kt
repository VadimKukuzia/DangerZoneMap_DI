package com.veseleil.dangerzonemap_di.ui.auth.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veseleil.dangerzonemap_di.data.remote.request.RegistrationRequest
import com.veseleil.dangerzonemap_di.data.remote.response.RegistrationResponse
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _registrationResponseLiveData = MutableLiveData<Response<RegistrationResponse>>()
    val registrationResponseLiveData get() = _registrationResponseLiveData

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            val response = repository.register(RegistrationRequest(username, email, password))
            _registrationResponseLiveData.postValue(response)
        }
    }
}