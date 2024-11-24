package com.example.criminalintent_2024

import android.app.TimePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar

class TimePickerFragment: DialogFragment() {

    private val args: TimePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ecouteurTime = TimePickerDialog.OnTimeSetListener { _, heure, minute ->
            val calendar = Calendar.getInstance() // Récupère une instance du calendrier actuel
            calendar.set(Calendar.HOUR_OF_DAY, heure) // Définit l'heure sélectionnée
            calendar.set(Calendar.MINUTE, minute)
            val timeResultat = calendar.time
            setFragmentResult(TIME_REQUEST_KEY, bundleOf(BUNDLE_KEY_TIME to timeResultat))
        }
        val c = Calendar.getInstance()
        c.time = args.timeIncident
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            ecouteurTime,
            hour,
            minute,
            true
        )
    }

    companion object {
        const val TIME_REQUEST_KEY = "TIME_REQUEST_KEY"
        const val BUNDLE_KEY_TIME = "BUNDLE_KEY_TIME"
    }
}