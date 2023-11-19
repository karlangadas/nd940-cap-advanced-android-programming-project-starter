package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.isOnline
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val application: Application): AndroidViewModel(application) {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean>
        get() = _saved

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>
        get() = _loading

    init {
        _loading.value = false
    }

    fun loadVoterInfo(
        address: String,
        electionId: Int
    ) {
        viewModelScope.launch {
            if (!isOnline(application)) {
                return@launch
            }
            _loading.value = true
            _voterInfo.value = electionsRepository.fetchVoterInfo(address, electionId)
            _saved.value = electionsRepository.isElectionSavedLocally(electionId)
            _loading.value = false
        }
    }

    fun removeElection(voterInfo: VoterInfoResponse) {
        viewModelScope.launch {
            electionsRepository.removeElectionById(voterInfo.election.id)
            _saved.value = false
        }
    }

    fun saveElection(voterInfo: VoterInfoResponse) {
        viewModelScope.launch {
            electionsRepository.saveElection(voterInfo.election)
            _saved.value = true
        }
    }

}