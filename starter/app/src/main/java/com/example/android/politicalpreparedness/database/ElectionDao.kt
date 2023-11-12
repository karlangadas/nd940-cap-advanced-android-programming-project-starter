package com.example.android.politicalpreparedness.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(election: Election)

    @Query("select * from election_table")
    fun getElections(): LiveData<List<Election>>

    @Query("select * from election_table where id =:id")
    fun getElectionById(id: Int): LiveData<Election>

    @Query("delete from election_table where id =:id")
    fun deleteElectionById(id: Int)

    @Query("delete from election_table")
    fun clear()
}