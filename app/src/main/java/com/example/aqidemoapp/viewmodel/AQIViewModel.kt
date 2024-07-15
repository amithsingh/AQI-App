package com.example.aqidemoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aqidemoapp.model.AQIResponse
import com.example.aqidemoapp.model.GeocodingResponse
import com.example.aqidemoapp.repository.AQIRepository
import com.example.aqidemoapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.aqidemoapp.utils.Result

@HiltViewModel
class AQIViewModel @Inject constructor(private val repository: AQIRepository) : ViewModel() {
    private val _aqiData = MutableStateFlow<Result<AQIResponse>?>(null)
    val aqiData: StateFlow<Result<AQIResponse>?> get() = _aqiData

    private val _geocodingData = MutableStateFlow<Result<GeocodingResponse>?>(null)
    val geocodingData: StateFlow<Result<GeocodingResponse>?> get() = _geocodingData

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun fetchAQI(lat: Double, lon: Double) {
        _loading.value = true
        viewModelScope.launch {
            repository.getAQI(lat, lon,Constants.AQIKey).collect {
                _aqiData.value = it
                _loading.value = false
            }
        }
    }

    fun fetchCoordinates(city: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.getCoordinates(city, Constants.geoCodingAPIKey).collect {
                    _geocodingData.value = it
                    if (it is Result.Success) {
                        val results = it.data.results
                        if (results.isNotEmpty()) {
                            val lat = results[0].geometry.lat
                            val lon = results[0].geometry.lng
                            fetchAQI(lat, lon)
                        } else {
                            _loading.value = false
                            _aqiData.value = Result.Failure(Exception("No results found for the city"))
                        }
                    } else {
                        _loading.value = false
                    }
                }
            }catch (e:Exception){
                _loading.value = false
                _aqiData.value = Result.Failure(Exception("No results found for the city"))
            }

        }
    }

    fun fetchCurrentLocationAQI(lat: Double, lon: Double) {
        fetchAQI(lat, lon)
    }

    fun clearAQIData() {
        _aqiData.value = null
    }

    fun clearGeocodingData() {
        _geocodingData.value = null
    }
}
