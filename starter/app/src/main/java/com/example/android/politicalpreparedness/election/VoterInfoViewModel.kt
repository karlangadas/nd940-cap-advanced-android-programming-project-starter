package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application): AndroidViewModel(application) {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _saved = MutableLiveData<Boolean>()
    val saved: LiveData<Boolean>
        get() = _saved

    fun loadVoterInfo(
        address: String,
        electionId: Int
    ) {
        viewModelScope.launch {
            _voterInfo.value = electionsRepository.fetchVoterInfo(address, electionId)
            _saved.value = electionsRepository.isElectionSavedLocally(electionId)
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

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}