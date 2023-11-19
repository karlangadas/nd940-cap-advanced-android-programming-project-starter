package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.models.Address
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "DetailFragment"
        //TODO: Add Constant for Location request
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
//
//        //TODO: Define and assign Representative adapter
//
//        //TODO: Populate Representative adapter
//
//        //TODO: Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissionsAndUseMyLocation()
        }
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationPermissionsAndUseMyLocation() {
        if (isPermissionGranted()) {
             getLocation()
        } else {
            requestFineLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionGranted() : Boolean =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

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
            Timber.tag(TAG).e(e, e.message)
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                Address(
                    address.thoroughfare?: "",
                    address.subThoroughfare,
                    address.locality?: "",
                    address.adminArea?: "",
                    address.postalCode?: "")
            }
                ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}