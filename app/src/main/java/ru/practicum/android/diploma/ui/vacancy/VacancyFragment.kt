package ru.practicum.android.diploma.ui.vacancy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.VacancyDetails

class VacancyFragment : Fragment() {
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private var isChecked = false
    private var url: String? = null

    private val viewModel by viewModel<VacancyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    url?.let { shareVacancy(it) }
                    true
                }
                R.id.action_like -> {
                    changeLikeButtonStatus(isChecked)
                    // Реализовать добавление вакансии в избранное
                    Log.d("log", "Like button clicked")
                    true
                }
                else -> false
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getVacancyDetails("117317594") //Передача данных с экрана "поиск" пока не сделана

        viewModel.getVacancyDetailsState().observe(viewLifecycleOwner) { vacancyDetailsState ->
            when (vacancyDetailsState) {
                is VacancyDetailsState.Loading -> {
                    binding.progress.isVisible = true
                    binding.vacancyContent.isVisible = false
                    binding.placeholder404Error.isVisible = false
                    binding.placeholderServerError.isVisible = false
                }
                is VacancyDetailsState.Content -> {
                    binding.progress.isVisible = false
                    binding.vacancyContent.isVisible = true
                    binding.placeholder404Error.isVisible = false
                    binding.placeholderServerError.isVisible = false
                    bindVacancyDetails(vacancyDetailsState.data)
                }
                is VacancyDetailsState.NotFoundError -> {
                    binding.progress.isVisible = false
                    binding.vacancyContent.isVisible = false
                    binding.placeholder404Error.isVisible = true
                    binding.placeholderServerError.isVisible = false
                }
                is VacancyDetailsState.ServerError -> {
                    binding.progress.isVisible = false
                    binding.vacancyContent.isVisible = false
                    binding.placeholder404Error.isVisible = false
                    binding.placeholderServerError.isVisible = true
                }
                else -> {}
            }
        }
    }

    private fun bindVacancyDetails(vacancyDetails: VacancyDetails) {
        /*url = vacancyDetails.url
        * такого поля пока что нет*/
        binding.nameText.text = vacancyDetails.vacancyName
        binding.salaryText.text = viewModel.getSalaryText(vacancyDetails.salaryFrom, vacancyDetails.salaryTo, vacancyDetails.currency)
        Glide.with(this)
            .load(vacancyDetails.logoUrl)
            .centerCrop()
            .transform(RoundedCorners((12f * resources.displayMetrics.density).toInt()))
            .placeholder(R.drawable.vacancy_placeholder)
            .into(binding.vacancyCardImage)
        binding.vacancyCardEmployerText.text = vacancyDetails.employer
        binding.vacancyCardEmployerText.isSelected = true
        /*Должен выводить address вместо региона.
        Регион выводится в том случае, если адрес не указан.
        Но в vacancyDetails нет такого поля*/
        binding.vacancyCardRegionText.text = vacancyDetails.area
        binding.vacancyCardRegionText.isSelected = true
        binding.experienceText.text = vacancyDetails.experience
        binding.workFormatText.text = vacancyDetails.workFormat?.joinToString(", ")
        binding.vacancyDescriptionText.text = vacancyDetails.description?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }
        if (!vacancyDetails.keySkills.isNullOrEmpty()) {
            binding.skillsText.text = viewModel.getSkillsText(vacancyDetails.keySkills)
        } else {
            binding.skillsLayout.isVisible = false
        }
    }

    private fun shareVacancy(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(shareIntent)

    }

    // Тестовая функция смены иконки избранной вакансии
    private fun changeLikeButtonStatus(value: Boolean) {
        val likeButton = binding.toolbar.menu.findItem(R.id.action_like)
        if (value) {
            likeButton.setIcon(R.drawable.ic_favourite_off)
            isChecked = false
        } else {
            likeButton.setIcon(R.drawable.ic_favourite_like)
            isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
