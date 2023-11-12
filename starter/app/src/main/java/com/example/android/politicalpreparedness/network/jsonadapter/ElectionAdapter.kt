package com.example.android.politicalpreparedness.network.jsonadapter

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date

class ElectionAdapter {
    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
            .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
            .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }

    @FromJson
    fun electionDateFromJson(electionDay: String): Date? {
        return try {
            SimpleDateFormat("YYY-MM-dd").parse(electionDay)
        } catch (ex: Exception) {
            Log.e("Exception in electionDateFromJson: ", ex.message.toString())
            null
        }
    }

    @ToJson
    fun electionDateToJson(electionDay: Date): String {
        return electionDay.toString()
    }
}