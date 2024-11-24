package com.example.criminalintent_2024

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent_2024.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.UUID

private const val FORMAT_DATE = "EEE,MMM, dd"

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "impossible binding car null, vue visible ?"
        }
    //private lateinit var incidant : Crime

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.incidentID)
    }

    private val choixSuspect = registerForActivityResult(ActivityResultContracts.PickContact()) {
        uri: Uri? ->
        uri?.let { traiterSelectionContact(it) }
    }

    private val prisePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        aPrisPhoto: Boolean ->
        //gestion du resultat
        if (aPrisPhoto && nomPhoto != null) {
            crimeDetailViewModel.majIncident {
                ancienIncident -> ancienIncident.copy(nomFichierPhoto = nomPhoto)
            }
        }
    }
    private var nomPhoto: String ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            //lien avec textedit
            etTitreCrime.doOnTextChanged{text, _, _, _ ->
                crimeDetailViewModel.majIncident {
                    ancienIncident -> ancienIncident.copy(titre = text.toString())
                }
            }
            //lien checkbox
            cbCrimeResolu.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.majIncident {
                    ancienIncident -> ancienIncident.copy(estResolu = isChecked)
                }
            }
            suspectIncident.setOnClickListener {
                choixSuspect.launch(null)
            }
            val choixSuspectIntent = choixSuspect.contract.createIntent(
                requireContext(),
                null
            )
            suspectIncident.isEnabled = peutResoudreIntent(choixSuspectIntent)

            prendrePhoto.setOnClickListener{
                nomPhoto = "IMG_${Date()}.JPG"
                val fichierPhoto = File(requireContext().applicationContext.filesDir, nomPhoto)
                val uriPhoto = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.criminalintent_2024.fileprovider",
                    fichierPhoto
                )
                prisePhoto.launch(uriPhoto)
            }
            val intentionPrisePhoto = prisePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )
            prendrePhoto.isEnabled = peutResoudreIntent(intentionPrisePhoto)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.incident.collect{
                    incident -> incident?.let {majUI(it)}
                }
            }
        }
        setFragmentResultListener(DatePickerFragment.DATE_REQUEST_KEY) {
            _, bundle ->
            val nouvelleDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.majIncident { it.copy(date = nouvelleDate) }
        }
        setFragmentResultListener(TimePickerFragment.TIME_REQUEST_KEY) {
                _, bundle ->
            val nouvelleTime = bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.majIncident { it.copy(date = nouvelleTime) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_crime_detail, menu)
    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.delete_incident -> {
//                deleteIncident(item)
//                true
//            }
//        }
//    }
//
//    private fun deleteIncident() {
//        crimeDetailViewModel.deleteIncident()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun majUI(incident: Crime) {
        binding.apply {
            if (etTitreCrime.text.toString() != incident.titre) {
                etTitreCrime.setText(incident.titre)
            }
            btnDateCrime.text = incident.date.toString()
            btnDateCrime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectionDate(incident.date)
                )
            }
            btnTimeCrime.text = incident.date.toString()
            btnTimeCrime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectionTime(incident.date)
                )
            }
            cbCrimeResolu.isChecked = incident.estResolu
            rapportIncident.setOnClickListener{
                val rapportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getRapportIncident(incident))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rapport_incident_sujet))
                }
                val selecteurIntent = Intent.createChooser(rapportIntent, getString(R.string.envoi_rapport))
                startActivity(selecteurIntent)
            }
            suspectIncident.text = incident.suspect.ifEmpty {
                getString(R.string.texte_choix_suspect)
            }
            majPhoto(incident.nomFichierPhoto)
        }
    }

    private fun getRapportIncident(incident: Crime): String {
        val textResolu = if(incident.estResolu) {
            getString(R.string.rapport_incident_resolu)
        } else {
            getString(R.string.rapport_incident_pas_resolu)
        }
        val textDate = DateFormat.format(FORMAT_DATE, incident.date).toString()
        val textSuspect = if (incident.suspect.isBlank()) {
            getText(R.string.rapport_incident_pas_suspect)
        } else {
            getString(R.string.rapport_incident_suspect, incident.suspect)
        }
        return getString(
            R.string.rapport_incident,
            incident.titre, textDate, textResolu, textSuspect
        )
    }

    private fun traiterSelectionContact(contactUri: Uri) {
        val champsRequete = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val pointeurRequete = requireActivity().contentResolver
            .query(contactUri, champsRequete, null, null, null)
        pointeurRequete?.use {pointeur ->
            if (pointeur.moveToFirst()) {
                val suspect = pointeur.getString(0)
                crimeDetailViewModel.majIncident { ancienIncident ->
                    ancienIncident.copy(suspect = suspect)
                }
            }
        }
    }

    private fun peutResoudreIntent(intention : Intent): Boolean {
        //simule l'absence de contact
//        intention.addCategory(Intent.CATEGORY_HOME)
        val packageManager: PackageManager = requireActivity().packageManager
        val activiteResolue: ResolveInfo? =
            packageManager.resolveActivity(
                intention,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return activiteResolue != null
    }

    private fun majPhoto(nomFichierPhoto: String?) {
        if(binding.photoIncident.tag != nomFichierPhoto) {
            val fichierPhoto = nomFichierPhoto?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (fichierPhoto?.exists() == true) {
                binding.photoIncident.doOnLayout {
                    vueMesuree -> val bitmapRedim = getBitmapRedim(
                        fichierPhoto.path,
                        vueMesuree.width,
                        vueMesuree.height
                    )
                    binding.photoIncident.setImageBitmap(bitmapRedim)
                    binding.photoIncident.tag = nomFichierPhoto
                }
            } else {
                binding.photoIncident.setImageBitmap(null)
                binding.photoIncident.tag = null
            }
        }
    }
}