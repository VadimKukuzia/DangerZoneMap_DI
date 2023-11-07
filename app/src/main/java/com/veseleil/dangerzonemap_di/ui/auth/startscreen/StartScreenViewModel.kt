package com.veseleil.dangerzonemap_di.ui.auth.startscreen

import androidx.lifecycle.ViewModel
import com.veseleil.dangerzonemap_di.data.repository.UserRepository
import javax.inject.Inject

class StartScreenViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {
    // TODO: Implement the ViewModel
}