package ru.practicum.android.diploma.ui.workplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.practicum.android.diploma.databinding.FragmentWorkplaceBinding


class WorkplaceFragment : Fragment() {
    private var _binding: FragmentWorkplaceBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<WorkplaceFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!args.countryId.isNullOrEmpty()){
            binding.countryEditText.setText(args.countryId)
        }
        if (!args.regionId.isNullOrEmpty()){
            binding.regionEditText.setText(args.regionId)
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.countryEditText.setOnClickListener {
            findNavController().navigate(WorkplaceFragmentDirections.actionWorkplaceFragmentToCountryFragment())
        }

        binding.regionEditText.setOnClickListener {
            findNavController().navigate(WorkplaceFragmentDirections.actionWorkplaceFragmentToRegionFragment())
        }

        binding.selectButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
