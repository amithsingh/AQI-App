@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.aqidemoapp.componets

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.aqidemoapp.R
import com.example.aqidemoapp.viewmodel.AQIViewModel
import com.example.aqidemoapp.utils.Result
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AQIScreen(viewModel: AQIViewModel) {
    var city by remember { mutableStateOf("") }
    val geocodingData by viewModel.geocodingData.collectAsState()
    val aqiData by viewModel.aqiData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current as? ComponentActivity ?: return
    val statusBarColor = colorResource(id = R.color.purple_500)
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var isFirstTime = true

    SideEffect {
        context.window.statusBarColor = statusBarColor.toArgb()
    }
    LaunchedEffect(locationPermissionState) {
        locationPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
       if(isFirstTime) {
           if (locationPermissionState.status.isGranted) {
               fetchCurrentLocation(context, viewModel)
               isFirstTime=false
           }
       }
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AQI Details", color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                },
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.purple_700))

            )
        }, content = {
            /*Used below view the show search filter*/
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Enter city name to get AQI Details") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.clearAQIData()
                    viewModel.clearGeocodingData()
                    viewModel.fetchCoordinates(city) }) {
                    Text("Get AQI")
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.purple_700),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    if (geocodingData is Result.Failure) {
                        Text(text = "Error fetching coordinates: ${(geocodingData as Result.Failure).exception.message}")
                    } else {
                        aqiData?.let {
                            when (it) {
                                is Result.Success -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                 val cityName = city.takeUnless { it.isBlank() } ?: it.data.data.city.name
                                                Text(
                                                    "AQI Detail of City: $cityName",
                                                    style = MaterialTheme.typography.headlineSmall
                                                )
                                                Text(
                                                    "City: ${it.data.data.city.name}",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    "AQI: ${it.data.data.aqi}",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    "Time: ${it.data.data.time.s}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                        }
                                    }

                                }

                                is Result.Failure -> {
                                    Text(text = "Error: ${it.exception.message}")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
@SuppressLint("MissingPermission")
fun fetchCurrentLocation(context: Context, viewModel: AQIViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            viewModel.fetchCurrentLocationAQI(it.latitude, it.longitude)
        }
    }
}
