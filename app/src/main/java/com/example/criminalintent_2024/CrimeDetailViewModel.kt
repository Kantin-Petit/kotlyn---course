package com.example.criminalintent_2024

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CrimeDetailViewModel(incidentID: UUID) : ViewModel() {
    private val depotIncidents = CrimeRepository.get()
    private val _incident: MutableStateFlow<Crime?> = MutableStateFlow(null)
    val incident: StateFlow<Crime?> = _incident.asStateFlow()

    init {
        viewModelScope.launch {
            _incident.value = depotIncidents.getIncident(incidentID)
        }
    }

    fun majIncident(onUpdate: (Crime) -> Crime) {
        _incident.update {
            ancienIncident -> ancienIncident?.let{onUpdate(it)}
        }
    }

    override fun onCleared() {
        super.onCleared()
        incident.value?.let {
            depotIncidents.majIncident(it)
        }
    }

//    suspend fun deleteIncident(incident: Crime) {
//        depotIncidents.deleteIncident(incident)
//    }
}

class CrimeDetailViewModelFactory(private val incidentID: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CrimeDetailViewModel(incidentID) as T
    }
}