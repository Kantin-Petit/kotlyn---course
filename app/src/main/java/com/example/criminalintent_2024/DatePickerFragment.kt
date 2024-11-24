package com.example.criminalintent_2024

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar
import java.util.GregorianCalendar

class DatePickerFragment: DialogFragment() {

    private val args: DatePickerFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ecouteurDate = DatePickerDialog.OnDateSetListener {
            _: DatePicker, annee: Int, mois: Int, jour: Int ->
            val dateResultat = GregorianCalendar(annee,mois,jour).time
            setFragmentResult(DATE_REQUEST_KEY, bundleOf(BUNDLE_KEY_DATE to dateResultat))
        }
        val calendrier = Calendar.getInstance()
        calendrier.time = args.dateIncident
        val annee = calendrier.get(Calendar.YEAR)
        val mois = calendrier.get(Calendar.MONTH)
        val jour = calendrier.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            ecouteurDate,
            annee,
            mois,
            jour
        )
    }

    companion object {
        const val DATE_REQUEST_KEY = "DATE_REQUEST_KEY"
        const val BUNDLE_KEY_DATE = "BUNDLE_KEY_DATE"
    }
}