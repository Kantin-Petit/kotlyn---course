package com.example.criminalintent_2024

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class CrimeListViewModel : ViewModel() {
    private val depotIncidents = CrimeRepository.get()
    //propriete liste incident
    private val _listeIncidents : MutableStateFlow<List<Crime>> = MutableStateFlow(emptyList())

    val listeIncidents : StateFlow<List<Crime>>
        get() = _listeIncidents.asStateFlow()

    //peuplement liste incident
    init {
        //demarage d'une nouvelle coroutine pour initialiser liste incident
        viewModelScope.launch {
            depotIncidents.getIncidents().collect{
                _listeIncidents.value = it
            }

        }
    }

    suspend fun ajoutIncident(incident: Crime)
    {
        depotIncidents.ajoutIncident(incident)
    }
}