package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.isOnline
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val application: Application, private val state: SavedStateHandle): AndroidViewModel(application) {


    private val repository = RepresentativeRepository()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val _addressLine1 = MutableLiveData<String>()
    val _addressLine2 = MutableLiveData<String>()
    val _city = MutableLiveData<String>()
    val _state = MutableLiveData<String>()
    val _zip = MutableLiveData<String>()
    val states =
        listOf("AL", "AK", "AZ", "AR", "CA", "NC", "SC", "CO", "CT", "ND", "SD", "DE",
        "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI",
        "MN", "MS", "MO", "MT", "NE", "NV", "NJ", "NY", "NH", "NM", "OH", "OK", "OR", "PA",
        "RI", "TN", "TX", "UT", "VT", "VT", "VA", "WV", "WA", "WI", "WY")

    init {
        _representatives.value =
            state.get<List<Representative>>(
                REPRESENTATIVES)
    }

    private fun fetchRepresentatives(addressStringFormat: String) {
        viewModelScope.launch {
            if (!isOnline(application)) {
                return@launch
            }
            val results: RepresentativeResponse = repository.refreshRepresentatives(addressStringFormat)
            _representatives.value = results.offices.flatMap { office ->
                office.getRepresentatives(results.officials)
            }
        }
    }

    fun setAddressFromLocation(address: Address) {
        _addressLine1.value = address.line1
        _addressLine2.value = address.line2 ?: ""
        _city.value = address.city
        _state.value = address.state
        _zip.value = address.zip

        fetchRepresentatives(address.toFormattedString())
    }

    fun saveState() {
        state[REPRESENTATIVES] = _representatives.value
    }

    companion object {
        private const val REPRESENTATIVES = "REPRESENTATIVES"
    }
}
