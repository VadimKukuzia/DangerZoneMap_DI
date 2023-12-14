package com.veseleil.dangerzonemap_di.ui.main.mainmap

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.data.model.Zone
import com.veseleil.dangerzonemap_di.databinding.FragmentMainMapBinding
import com.veseleil.dangerzonemap_di.geofence.GeofenceManager
import com.veseleil.dangerzonemap_di.utils.SessionManager
import com.veseleil.dangerzonemap_di.utils.ZoneInfoWindowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var _binding: FragmentMainMapBinding
    private val binding get() = _binding

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel: MainMapViewModel by viewModels()

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    private lateinit var geofenceManager: GeofenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.locationFloatingActionButton.setOnClickListener {
            getLastLocation()
        }

        geofenceManager = GeofenceManager(requireContext())

        viewModel.getZonesToShow("Bearer " + sessionManager.fetchAccessToken().toString())

        viewModel.zonesResponseLiveData.observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                val zones = it.body()!!
                Log.d("LOGTAG", zones.isEmpty().toString())
                if (zones.isNotEmpty()) {
                    deregisterGeofences()

                    val requiredZoneTypes: MutableSet<String> =
                        sessionManager.fetchZoneTypes() ?: mutableSetOf("NT", "TG", "AG", "EC")
                    val zonesToShowList =
                        zones.filter { zone -> requiredZoneTypes.contains(zone.zoneType) }

                    if (this::map.isInitialized) {
                        displayZonesToShow(zonesToShowList)
                        addGeofences(zonesToShowList)
                    } else {
                        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
                    }
                } else {
                    Toast.makeText(requireContext(), "No zones to show", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("LOGTAG", "Not successful")
            }
        }

    }

    private fun deregisterGeofences() {
        lifecycleScope.launch {
            geofenceManager.deregisterGeofence()
        }
    }

    private fun addGeofences(zonesToShowList: List<Zone>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (isBackgroundPermissionGiven()) {
                // Adding geofences
                zonesToShowList.forEach {zone ->
                    geofenceManager.addGeofence(
                        zone.name,
                        LatLng(zone.lat, zone.lon),
                        zone.radius.toFloat()
                    )
                }
                geofenceManager.registerGeofence()
            } else {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        } else {
            // Adding geofences
            zonesToShowList.forEach {zone ->
                geofenceManager.addGeofence(
                    zone.name,
                    LatLng(zone.lat, zone.lon),
                    zone.radius.toFloat()
                )
            }
            geofenceManager.registerGeofence()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun isBackgroundPermissionGiven(): Boolean {
        return ContextCompat
            .checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun displayZonesToShow(zonesToShowList: List<Zone>) {
        zonesToShowList.forEach { zone ->
            val zoneLatLng = LatLng(zone.lat, zone.lon)
            map.addCircle(
                CircleOptions()
                    .center(zoneLatLng)
                    .radius(zone.radius.toDouble())
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.argb(80, 255, 0, 0))
            )
            val marker = map.addMarker(
                MarkerOptions()
                    .alpha(0f)
                    .position(zoneLatLng)
            )
            marker?.tag = zone
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = false

        map.clear()
        map.uiSettings.isMapToolbarEnabled = false
        map.setInfoWindowAdapter(ZoneInfoWindowAdapter(requireContext()))

        // 1 - Show user his location
        getLastLocation()
    }

    private fun getLastLocation() {
        Log.d("LOGTAG", "user permission" + checkUserPermission().toString())
        if (checkUserPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        map.clear()
                        map.animateCamera((CameraUpdateFactory.newLatLngZoom(currentLocation, 12F)))
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_enable_text),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                getLastLocation()
            }
        } else {
            Log.d("LOGTAG", "build version")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.d("LOGTAG", "build version > Q")
                getPermissionsWithSettings()
            } else {
                Log.d("LOGTAG", "build version < Q")
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun checkUserPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateDistanceMeters(1f)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation!!
            currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                map.isMyLocationEnabled = true
                getLastLocation()
                Log.d("LOGTAG", "Fine location granted")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                map.isMyLocationEnabled = true
                getLastLocation()
                Log.d("LOGTAG", "Coarse location granted")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                map.isMyLocationEnabled = true
                getLastLocation()
            }
            else -> {
                // No location access granted.
                getPermissionsWithSettings()
            }
        }
    }

    private fun getPermissionsWithSettings() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.permission_grant_title)
            setMessage(R.string.permission_grant_text)
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                    startActivity(intent)
                }
            )
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
            )
            create()
            show()
        }
        Log.d("LOGTAG", "No location granted")
    }

}