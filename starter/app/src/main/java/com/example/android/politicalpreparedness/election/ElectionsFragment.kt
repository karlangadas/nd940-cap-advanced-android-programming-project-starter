package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionClick
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        upcomingElectionsAdapter = ElectionListAdapter(ElectionClick { election ->
            findNavController()
                .navigate(
                    ElectionsFragmentDirections
                        .actionShowVoterInfo(election.id))
            })

        savedElectionsAdapter = ElectionListAdapter(ElectionClick { election ->
            findNavController()
                .navigate(
                    ElectionsFragmentDirections
                        .actionShowVoterInfo(election.id))
        })

        binding.upcomingElectionsRecycler.adapter = upcomingElectionsAdapter
        binding.savedElectionsRecycler.adapter = savedElectionsAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                upcomingElectionsAdapter.submitList(it.elections)
            }
        }

//        viewModel.savedElections.observe(viewLifecycleOwner) {
//            it?.let {
//                adapter.submitList(it)
//            }
//        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadElections()
    }
}