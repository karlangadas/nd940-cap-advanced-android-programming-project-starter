package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoFragment : Fragment() {

    lateinit var viewModel: VoterInfoViewModel

    private val electionId: Int by lazy {
        VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
    }

    private val state: String by lazy {
        VoterInfoFragmentArgs.fromBundle(requireArguments()).argState
    }

    private val country: String by lazy {
        VoterInfoFragmentArgs.fromBundle(requireArguments()).argCountry
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[VoterInfoViewModel::class.java]

        binding.viewModel = viewModel

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }
        }

        viewModel.saved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                binding.followButton.setText(R.string.unfollow_election)
            }
            else {
                binding.followButton.setText(R.string.follow_election)
            }
        }

        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            val state = voterInfo?.state
            if (state.isNullOrEmpty()) {
                binding.electionInformation.visibility = View.GONE
                binding.votingLocations.visibility = View.GONE
                binding.ballotInformation.visibility = View.GONE
                binding.stateCorrespondenceHeader.visibility = View.GONE
                binding.followButton.visibility = View.GONE
                return@observe
            }
            val body = state[0].electionAdministrationBody

            body.correspondenceAddress?.also {
                binding.stateCorrespondenceHeader.visibility = View.VISIBLE
            }
            binding.followButton.visibility = View.VISIBLE

            val electionInfoUrl = body.electionInfoUrl
            binding.electionInformation.apply {
                if (electionInfoUrl.isNullOrEmpty()){
                    visibility = View.GONE
                }
                else {
                    visibility = View.VISIBLE
                    setOnClickListener {
                            val browserIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(electionInfoUrl))
                            startActivity(browserIntent)
                    }
                }
            }

            val votingLocationFinderUrl = body.votingLocationFinderUrl
            binding.votingLocations.apply {
                if (electionInfoUrl.isNullOrEmpty()){
                    visibility = View.GONE
                }
                else {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(votingLocationFinderUrl))
                        startActivity(browserIntent)
                    }
                }
            }

            val ballotInfoUrl = body.ballotInfoUrl
            binding.ballotInformation.apply {
                if (electionInfoUrl.isNullOrEmpty()){
                    visibility = View.GONE
                }
                else {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(ballotInfoUrl))
                        startActivity(browserIntent)
                    }
                }
            }
        }

        binding.followButton.setOnClickListener {
            viewModel.voterInfo.value?.also { voterInfo ->
                if (viewModel.saved.value == true) {
                    viewModel.removeElection(voterInfo)
                } else {
                    viewModel.saveElection(voterInfo)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = state.ifEmpty { "CA" } //fallback
        val country = country.ifEmpty { "USA" } //fallback
        val address = " , , $state, $country"
        viewModel.loadVoterInfo(address, electionId)
    }
}