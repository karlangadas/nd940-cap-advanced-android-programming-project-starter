package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {


    private val repository = RepresentativeRepository()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives : LiveData<List<Representative>>
    get() = _representatives

    val _addressLine1 = MutableLiveData<String>()
    val addressLine1: LiveData<String>
        get() = _addressLine1

    val _addressLine2 = MutableLiveData<String>()
    val addressLine2: LiveData<String>
        get() = _addressLine2

    val _city = MutableLiveData<String>()
    val city: LiveData<String>
        get() = _city

    val _state = MutableLiveData<String>()
    val state: LiveData<String>
        get() = _state

    val _zip = MutableLiveData<String>()
    val zip: LiveData<String>
        get() = _zip

    fun fetchRepresentatives(addressStringFormat: String) {
        viewModelScope.launch {
            val results: RepresentativeResponse = repository.refreshRepresentatives(addressStringFormat)
            _representatives.value = results.offices.flatMap { office ->
                office.getRepresentatives(results.officials)
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun setAddressFromLocation(address: Address) {
        _addressLine1.value = address.line1
        _addressLine2.value = address.line2 ?: ""
        _city.value = address.city
        _state.value = address.state
        _zip.value = address.zip

//        fetchRepresentatives(address.toFormattedString())
    }

    //TODO: Create function to get address from individual fields

}
