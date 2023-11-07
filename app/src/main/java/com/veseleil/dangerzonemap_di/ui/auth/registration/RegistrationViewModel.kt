package com.veseleil.dangerzonemap_di.ui.auth.registration

import androidx.lifecycle.ViewModel
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

}