package com.example.criminalintent_2024

import android.content.Context
import androidx.room.Room
import com.example.criminalintent_2024.bd.BDIncident
import com.example.criminalintent_2024.bd.migration_1_2
import com.example.criminalintent_2024.bd.migration_2_3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val NOM_BD = "BDIncident"

class CrimeRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
){
    private val database: BDIncident = Room
        .databaseBuilder(
            context.applicationContext,
            BDIncident::class.java,
            NOM_BD
        )
        .addMigrations(migration_1_2, migration_2_3)
        .build()
    //correspond a getIncidents de crimeDAO
    fun getIncidents(): Flow<List<Crime>> = database.crimeDAO().getIncidents()
    //correspond a getIncident de crime DAO
    suspend fun getIncident(id:UUID) = database.crimeDAO().getIncident(id)

    fun majIncident(incident: Crime) {
        coroutineScope.launch {
            database.crimeDAO().majIncident(incident)
        }
    }

    suspend fun ajoutIncident(incident: Crime) {
        database.crimeDAO().ajoutIncident(incident)
    }

//    fun deleteIncident(incident: Crime) {
//        database.crimeDAO().deleteIncident(incident)
//    }

    companion object{
        private var INSTANCE: CrimeRepository? = null

        fun initialiser(context: Context){
            if (INSTANCE == null){
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository{
            return INSTANCE ?:
            throw IllegalStateException("Crime repository doit d'abord etre iniatiliser")
        }
    }
}