package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.ElectionResponse

class ElectionRepository(private val database: ElectionDatabase) {

    suspend fun refreshUpcomingElections() : ElectionResponse {
        return CivicsApi.retrofitService.getElectionsAsync()
    }

}