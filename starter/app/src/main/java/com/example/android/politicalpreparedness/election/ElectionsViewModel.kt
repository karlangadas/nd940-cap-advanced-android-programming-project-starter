package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class ElectionsViewModel(application: Application): AndroidViewModel(application) {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionRepository(database)

    private val _upcomingElections = MutableLiveData<ElectionResponse>()
    val upcomingElections: LiveData<ElectionResponse>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>
    get() = _loading

    init {
        _loading.value = false
    }

    fun loadElections() {
        viewModelScope.launch {
            _loading.value = true
            refreshUpcomingElections()
            getSavedElections()
            _loading.value = false
        }
    }

    private suspend fun refreshUpcomingElections() {
        _upcomingElections.value = electionsRepository.refreshUpcomingElections()
    }

    private suspend fun getSavedElections() {
        _savedElections.value = electionsRepository.savedElections()
    }
}