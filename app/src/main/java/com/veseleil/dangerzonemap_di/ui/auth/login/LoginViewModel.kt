package com.veseleil.dangerzonemap_di.ui.auth.login

import androidx.lifecycle.ViewModel
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

}