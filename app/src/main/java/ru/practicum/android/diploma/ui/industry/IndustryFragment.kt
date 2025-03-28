package ru.practicum.android.diploma.ui.industry

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentIndustryBinding
import ru.practicum.android.diploma.domain.models.Industries
import ru.practicum.android.diploma.domain.models.Industry

class IndustryFragment : Fragment() {
    private var _binding: FragmentIndustryBinding? = null
    private val binding get() = _binding!!

    private var industryAdapter: IndustryAdapter? = null
    private var inputMethod: InputMethodManager? = null

    private val viewModel by viewModel<IndustryViewModel>()

    var industries = mutableListOf<Industry>()
    var selectedIndustry: Industry? = null

    var firstTimeShowIndustries = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getIndustryFilter()

        inputMethod = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        industryAdapter = IndustryAdapter(industries, onChoosedIndustry)

        binding.industriesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.industriesList.adapter = industryAdapter

        viewModel.searchIndustries(EMPTY_STRING)

        viewModel.getGetIndustriesState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        val inputTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // здесь можно не реализовывать
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearOrSearchButton.setImageResource(R.drawable.ic_search)
                } else {
                    binding.clearOrSearchButton.setImageResource(R.drawable.ic_close)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.getIndustriesErrorLayout.isVisible = false
                binding.noIndustryFoundLayout.isVisible = false
                binding.noInternetLayout.isVisible = false

                viewModel.searchIndustries(s.toString())
            }
        }

        binding.industryEdittext.addTextChangedListener(inputTextWatcher)

        binding.industryEdittext.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                inputMethod?.hideSoftInputFromWindow(binding.industryEdittext.windowToken, 0)
            }
            false
        }

        binding.clearOrSearchButton.setOnClickListener { binding.industryEdittext.setText(EMPTY_STRING) }

        binding.buttonApply.setOnClickListener {
            setFragmentResult(
                FILTER_KEY,
                bundleOf(
                    INDUSTRY_NAME to selectedIndustry?.industryName,
                    INDUSTRY_ID to selectedIndustry?.industryId
                )
            )
            findNavController().navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun hideKeyBoard() {
        val inputMethod = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(binding.industryEdittext.windowToken, 0)
    }

    val onChoosedIndustry: (selectedIndustry: Industry?) -> Unit = { selected ->
        binding.buttonApply.isVisible = true

        selectedIndustry = selected
    }

    fun renderState(state: GetIndustriesState?) {
        when (state) {
            is GetIndustriesState.Loading -> showProgress()
            is GetIndustriesState.Error -> showError(state.errorCode)
            is GetIndustriesState.Content -> showIndustries(state.data)
            else -> {
            }
        }
    }

    private fun showProgress() {
        binding.progress.isVisible = true
    }

    private fun hideProgress() {
        binding.progress.isVisible = false
    }

    private fun showError(errorCode: Int) {
        industries.clear()
        industryAdapter?.notifyDataSetChanged()

        binding.buttonApply.isVisible = false

        hideProgress()

        when (errorCode) {
            -1 -> binding.noInternetLayout.isVisible = true
            else -> binding.getIndustriesErrorLayout
        }
        hideKeyBoard()
    }

    private fun showIndustries(data: Industries) {
        industries.clear()
        industries.addAll(data.items)

        hideProgress()

        if (industries.isEmpty()) {
            binding.noIndustryFoundLayout.isVisible = true
            binding.buttonApply.isVisible = false
            hideKeyBoard()
        } else {
            binding.noIndustryFoundLayout.isVisible = false
            checkSelectedIndustry(selectedIndustry)
        }

        if (firstTimeShowIndustries && selectedIndustry != null) {
            var position = -1
            for (ind in industries.indices) {
                if (industries[ind].industryId == selectedIndustry!!.industryId) {
                    position = ind
                    break
                }
            }

            if (position > 0) {
                position--
            }

            industryAdapter?.setSelectedIndutsry(selectedIndustry)

            if (position != -1) {
                binding.industriesList.layoutManager?.scrollToPosition(position)
            }
            firstTimeShowIndustries = false
        }
        industryAdapter?.notifyDataSetChanged()
    }

    private fun getIndustryFilter() {
        var industryId: String? = null
        var industryName: String? = null

        setFragmentResultListener(INDUSTRY_KEY) { _, bundle ->
            bundle.keySet().forEach { key ->
                val value = bundle.getString(key)
                if (value != null) {
                    when (key) {
                        INDUSTRY_ID -> {
                            industryId = value
                        }

                        INDUSTRY_NAME -> {
                            industryName = value
                        }

                        else -> {}
                    }
                }
            }
            selectedIndustry = Industry(industryId.orEmpty(), industryName.orEmpty())
        }
    }

    private fun checkSelectedIndustry(selectedIndustry: Industry?) {
        binding.buttonApply.isVisible = selectedIndustry != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val INDUSTRY_ID = "industryId"
        const val INDUSTRY_NAME = "industryName"
        const val FILTER_KEY = "filter_key"
        const val INDUSTRY_KEY = "industry_key"
        const val EMPTY_STRING = ""
    }
}
