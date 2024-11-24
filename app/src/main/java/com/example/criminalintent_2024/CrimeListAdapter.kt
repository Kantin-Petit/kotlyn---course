package com.example.criminalintent_2024

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent_2024.databinding.ListItemCrimeBinding
import java.text.DateFormat
import java.util.Locale
import java.util.UUID

class CrimeHolder (val binding: ListItemCrimeBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(incident: Crime, onCrimeClicked: (incidentID: UUID) -> Unit){
        binding.crimeTitre.text = incident.titre
        binding.crimeDate.text = incident.date.toString()
        binding.root.setOnClickListener{
            //Toast.makeText(binding.root.context,"click sur ${incident.titre}", Toast.LENGTH_SHORT).show()
            onCrimeClicked(incident.id)
        }
        binding.resolu.visibility = if (incident.estResolu){
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
class CrimeListAdapter(private val incidents : List<Crime>, private val onCrimeClicked: (incidentID: UUID) -> Unit) : RecyclerView.Adapter<CrimeHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater,parent,false)
        return CrimeHolder(binding)
    }

    override fun getItemCount() = incidents.size

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val incident = incidents[position]
        holder.bind(incident, onCrimeClicked)
    }

}