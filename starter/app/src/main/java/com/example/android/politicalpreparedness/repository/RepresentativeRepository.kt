package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

class RepresentativeRepository() {
    suspend fun refreshRepresentatives(address: String) : RepresentativeResponse {
        return CivicsApi.retrofitService.getRepresentativesAsync(address)
    }
}