package com.supermarche.tchad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.supermarche.tchad.databinding.FragmentSettingsBinding

// Settings screen: language selection, app info, currency and about
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check the radio button of the language currently in use
        val currentLanguage = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
        if (currentLanguage == "fr") binding.rbFrench.isChecked = true else binding.rbEnglish.isChecked = true

        // Change the language when the user picks another one.
        // Android recreates the screen automatically with the new translations.
        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val languageCode = if (checkedId == R.id.rbFrench) "fr" else "en"
            if (languageCode != currentLanguage) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
