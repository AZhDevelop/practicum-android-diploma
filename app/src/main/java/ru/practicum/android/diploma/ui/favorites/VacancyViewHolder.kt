package ru.practicum.android.diploma.ui.favorites

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val vacancy: TextView = itemView.findViewById(R.id.vacancy)
    private val company: TextView = itemView.findViewById(R.id.company)
    private val salary: TextView = itemView.findViewById(R.id.salary)
    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(item: Vacancy) {
        val itemSalary = if (item.salaryFrom == null && item.salaryTo == null) {
            "Зарплата не указана"
        } else if (item.salaryFrom == null && item.salaryTo != null) {
            item.salaryTo.toString()
        } else if (item.salaryFrom != null && item.salaryTo == null) {
            item.salaryFrom.toString()
        } else {
            item.salaryFrom.toString() + " " + item.salaryTo.toString()
        }
        val itemName = item.vacancyName + ", " + item.area
        vacancy.text = itemName
        company.text = item.employer
        salary.text = itemSalary

        Glide.with(itemView)
            .load(item.logoUrl)
            .centerCrop()
            .placeholder(R.drawable.img_job_placeholder)
            .into(image)
    }
}
