package com.veseleil.dangerzonemap_di.ui.main.addzonemap

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
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
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
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.veseleil.dangerzonemap_di.R
import com.veseleil.dangerzonemap_di.databinding.FragmentAddZoneMapBinding
import com.veseleil.dangerzonemap_di.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class AddZoneMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var _binding: FragmentAddZoneMapBinding
    private val binding get() = _binding

    private val viewModel: AddZoneViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var map: GoogleMap

    private var newZoneCircle: Circle? = null

    private lateinit var zoneTypes: Map<String, String>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddZoneMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        binding.sliderValue.text = binding.slider.value.roundToInt().toString()

        BottomSheetBehavior.from(binding.newZoneFrameLayout).apply {
            peekHeight = 140
            this.state = BottomSheetBehavior.STATE_COLLAPSED
            this.isHideable = false
        }

        zoneTypes = mapOf(
            requireContext().getString(R.string.natural) to "NT",
            requireContext().getString(R.string.technogenic) to "TG",
            requireContext().getString(R.string.anthropogenic) to "AG",
            requireContext().getString(R.string.ecological) to "EC"
        )

        setZoneTypeAdapter()

        binding.slider.addOnChangeListener { _, value, _ ->
            binding.sliderValue.text = String.format("%.1f", value)
            newZoneCircle?.radius = value.toDouble() * 1000
        }

        binding.btnAddZoneSubmit.setOnClickListener {
            if (newZoneCircle == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.choose_point_on_map_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addNewZone()
            }
        }
    }

    private fun addNewZone() {
        val name = binding.tiNewZoneTitle.editText?.text.toString()
        val zoneType = zoneTypes[binding.chooseZoneTypeTextView.text.toString()].toString()
        val latLng = newZoneCircle?.center!!
        val radius = newZoneCircle?.radius!!.toInt()
        val description = binding.extraData.editText?.text.toString()

        if (name.isNotBlank() and zoneType.isNotBlank() and description.isNotBlank()) {
            val accessToken = "Bearer " + sessionManager.fetchAccessToken().toString()
            viewModel.addNewZone(
                accessToken, name, zoneType, latLng.latitude, latLng.longitude, radius, description
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.new_zone_inform_sent),
                Toast.LENGTH_SHORT
            ).show()
            clearScreen()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.enter_new_zone_values),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearScreen() {
        map.clear()
        binding.tiNewZoneTitle.editText?.setText("")
        binding.extraData.editText?.setText("")
        BottomSheetBehavior.from(binding.newZoneFrameLayout).state =
            BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setZoneTypeAdapter() {

        val zoneTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.zone_type_list_item,
            zoneTypes.keys.toTypedArray()
        )

        binding.chooseZoneTypeTextView.setAdapter(zoneTypeAdapter)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isMyLocationButtonEnabled = false

        // 1 - Show user his location
        getLastLocation()

        // 2 - Give opportunity to create new danger zone
        map.setOnMapClickListener {
            map.clear()
            binding.slider.value = 1F
            newZoneCircle = map.addCircle(
                CircleOptions()
                    .center(it)
                    .radius(1000.0)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.argb(80, 255, 0, 0))
            )
        }
    }

    private fun getLastLocation() {
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
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkUserPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
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
            else -> {
                // No location access granted.
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
    }

}