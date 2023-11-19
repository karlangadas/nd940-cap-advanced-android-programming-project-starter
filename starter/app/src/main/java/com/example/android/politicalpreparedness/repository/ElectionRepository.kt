package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

class ElectionRepository(private val database: ElectionDatabase) {

    suspend fun refreshUpcomingElections() : ElectionResponse {
        return CivicsApi.retrofitService.getElectionsAsync()
    }

    suspend fun getVoterInfo(address: String,
                             electionId: Int) : VoterInfoResponse {
        return CivicsApi.retrofitService.getVoterInfoAsync(address, electionId)
    }
}