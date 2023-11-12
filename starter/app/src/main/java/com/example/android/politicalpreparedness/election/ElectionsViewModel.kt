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

    fun loadElections() {
        viewModelScope.launch {
                refreshUpcomingElections()
        }
    }

    private suspend fun refreshUpcomingElections() {
        _upcomingElections.value = electionsRepository.refreshUpcomingElections()
    }

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}