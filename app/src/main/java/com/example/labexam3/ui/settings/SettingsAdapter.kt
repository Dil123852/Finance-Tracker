package com.example.labexam3.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.labexam3.databinding.ItemSettingBinding
import com.example.labexam3.utils.PreferencesManager

class SettingsAdapter(private val preferencesManager: PreferencesManager) :
    RecyclerView.Adapter<SettingsAdapter.SettingViewHolder>() {

    private val settings = listOf(
        SettingItem("Monthly Budget", preferencesManager.monthlyBudget.toString()),
        SettingItem("Currency", preferencesManager.currency),
        SettingItem("Notifications", if (preferencesManager.notificationsEnabled) "Enabled" else "Disabled")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding = ItemSettingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SettingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.bind(settings[position])
    }

    override fun getItemCount() = settings.size

    class SettingViewHolder(private val binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(setting: SettingItem) {
            binding.settingTitle.text = setting.title
            binding.settingValue.text = setting.value
        }
    }

    data class SettingItem(val title: String, val value: String)
} 