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

        viewModel = ViewModelProvider(this)[ElectionsViewModel::class.java]

        binding.viewModel = viewModel

        upcomingElectionsAdapter = ElectionListAdapter(ElectionClick { election ->
            findNavController()
                .navigate(
                    ElectionsFragmentDirections
                        .actionShowVoterInfo(election.id, election.division.state, election.division.country))
            })

        savedElectionsAdapter = ElectionListAdapter(ElectionClick { election ->
            findNavController()
                .navigate(
                    ElectionsFragmentDirections
                        .actionShowVoterInfo(election.id, election.division.state, election.division.country))
        })

        binding.upcomingElectionsRecycler.adapter = upcomingElectionsAdapter
        binding.savedElectionsRecycler.adapter = savedElectionsAdapter

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.progress1.visibility = View.VISIBLE
                binding.progress2.visibility = View.VISIBLE
                binding.upcomingElectionsRecycler.visibility = View.GONE
                binding.savedElectionsRecycler.visibility = View.GONE
            } else {
                binding.progress1.visibility = View.GONE
                binding.progress2.visibility = View.GONE
                binding.upcomingElectionsRecycler.visibility = View.VISIBLE
                binding.savedElectionsRecycler.visibility = View.VISIBLE
            }
        }

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                upcomingElectionsAdapter.submitList(it.elections)
                if (it.elections.isNotEmpty()) {
                    binding.upcomingElectionsRecycler.visibility =  View.VISIBLE
                    binding.noResults1.visibility =  View.GONE
                }
                else {
                    binding.noResults1.visibility =  View.VISIBLE
                    binding.upcomingElectionsRecycler.visibility = View.GONE
                }
            }
        }

        viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let {
                savedElectionsAdapter.submitList(it)
                if (it.isNotEmpty()) {
                    binding.savedElectionsRecycler.visibility = View.VISIBLE
                    binding.noResults2.visibility =  View.GONE
                }
                else {
                    binding.noResults2.visibility =  View.VISIBLE
                    binding.savedElectionsRecycler.visibility = View.GONE
                }
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadElections()
    }
}