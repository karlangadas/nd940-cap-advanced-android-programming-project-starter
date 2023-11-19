package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoFragment : Fragment() {

    lateinit var viewModel: VoterInfoViewModel

    private val electionId: Int by lazy {
        VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
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

        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            val state = voterInfo?.state
            if (state.isNullOrEmpty()) {
                binding.electionInformation.visibility = View.GONE
                binding.votingLocations.visibility = View.GONE
                binding.ballotInformation.visibility = View.GONE
                binding.stateCorrespondenceHeader.visibility = View.GONE
                return@observe
            }
            val body = state[0].electionAdministrationBody

            body.correspondenceAddress?.also {
                binding.stateCorrespondenceHeader.visibility = View.VISIBLE
            }

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

        // TODO: Handle save button UI state
        // TODO: cont'd Handle save button clicks
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // todo: Implement geo
        val address = "Unnamed Road, Mangum, OK 73554, USA"
        viewModel.loadVoterInfo(address, electionId)
    }
}