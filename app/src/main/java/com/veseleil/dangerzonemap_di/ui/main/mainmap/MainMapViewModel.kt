package com.veseleil.dangerzonemap_di.ui.main.mainmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veseleil.dangerzonemap_di.data.model.Zone
import com.veseleil.dangerzonemap_di.data.repository.ZoneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val repository: ZoneRepository
) : ViewModel() {

    private val _zonesResponseLiveData = MutableLiveData<Response<List<Zone>>>()
    val zonesResponseLiveData: LiveData<Response<List<Zone>>> = _zonesResponseLiveData

    fun getZonesToShow(accessToken: String){
        viewModelScope.launch {
            val response = repository.getZonesToShow(accessToken)
            _zonesResponseLiveData.postValue(response)
        }
    }
}