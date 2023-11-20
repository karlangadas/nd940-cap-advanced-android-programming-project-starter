package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.ElectionsFragmentDirections
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.adapter.ElectionClick
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "DetailFragment"
    }

    private val requestFineLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkLocationPermissionsAndUseMyLocation()
            } else {
                // Permission request cancelled or permission denied.
                Snackbar.make(
                    this.requireView(),
                    R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        // Displays App settings screen.
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }

    private lateinit var representativedAdapter: RepresentativeListAdapter
    lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[RepresentativeViewModel::class.java]

        binding.viewModel = viewModel

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())


        initSpinner()

        representativedAdapter = RepresentativeListAdapter()
        binding.representativesRecycler.adapter = representativedAdapter

        viewModel.representatives.observe(viewLifecycleOwner) {
            it?.also {
                representativedAdapter.submitList(it)
            }
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissionsAndUseMyLocation()
        }
        binding.buttonSearch.setOnClickListener {
            checkLocationPermissionsAndUseFields()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    //https://www.geeksforgeeks.org/spinner-in-kotlin/
    private fun initSpinner() {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            viewModel.states)
        binding.state.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermissionsAndUseMyLocation() {
        if (isPermissionGranted()) {
            checkDeviceLocationSettingsAndGetLocation()
        } else {
            requestFineLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionGranted() : Boolean =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private val locationServiceEnabledRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            checkDeviceLocationSettingsAndGetLocation()
        }
        else {
            Snackbar.make(
                requireView(),
                R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
                checkDeviceLocationSettingsAndGetLocation()
            }.show()
        }
    }

    private fun checkDeviceLocationSettingsAndGetLocation() {
        // create a location request
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        // create location setting request builder
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            // if location settings are not satisfied
            if (exception is ResolvableApiException) {
                // but this can be fixed by showing the user a dialog.
                try {
                    locationServiceEnabledRequestLauncher
                        .launch(IntentSenderRequest.Builder(exception.resolution).build())
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.tag(TAG)
                        .d("Error getting location settings resolution: ${sendEx.message}")
                }
            } else {
                // present a snackbar that alerts the user that location needs to be enabled
                Snackbar.make(
                    requireView(),
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndGetLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }
     private fun getLocation() {
        try {
            if (isPermissionGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        task.result?.let { lastKnownLocation ->
                            val location = Location("").apply{
                                latitude = lastKnownLocation.latitude
                                longitude = lastKnownLocation.longitude
                            }
                            val address = geoCodeLocation(location)
                            if (address != null) {
                                viewModel.setAddressFromLocation(address)
                            }
                            else {
                            Toast.makeText(requireContext(), R.string.location_error, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), R.string.location_error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.tag(TAG).e(e)
        }
    }

    private fun checkLocationPermissionsAndUseFields() {
        val address = Address(
            binding.addressLine1.text.toString(),
            binding.addressLine2.text.toString(),
            binding.city.text.toString(),
            binding.state.selectedItem.toString(),
            binding.zip.text.toString()
            )
        hideKeyboard()
        viewModel.setAddressFromLocation(address)
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare?: return null,
                    address.subThoroughfare,
                    address.locality?: return null,
                    address.adminArea?: return null,
                    address.postalCode?: return null)
//                Address(
//                    "Unnamed Road",
//                    null,
//                    "Magnum",
//                    "OK",
//                    "73554")
            }
                ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}