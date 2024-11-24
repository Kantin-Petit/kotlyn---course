package com.example.criminalintent_2024

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintent_2024.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

//private const val TAG ="CrimeListFragment"
class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding){
            "impossible binding car null, vue visible ?"
        }

    private val crimeListViewModel : CrimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(inflater, container,false)
        //ajout layout manager
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeListViewModel.listeIncidents.collect {
                    listeIncidents -> binding.crimeRecyclerView.adapter = CrimeListAdapter(listeIncidents) {
                        incidentID -> findNavController().navigate(
                            CrimeListFragmentDirections.afficheDetailIncident(incidentID)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nouvel_incident -> {
                afficherNouvelIncdent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun afficherNouvelIncdent() {
        viewLifecycleOwner.lifecycleScope.launch {
            val nouvelIncident = Crime (
                id = UUID.randomUUID(),
                titre = "",
                date = Date(),
                estResolu = false
            )
            crimeListViewModel.ajoutIncident(nouvelIncident)
            findNavController().navigate(CrimeListFragmentDirections.afficheDetailIncident(nouvelIncident.id))
        }

    }
}
