package com.example.criminalintent_2024

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime(
    @PrimaryKey val id : UUID,
    @ColumnInfo (name = "title")val titre : String,
    val date : Date,
    @ColumnInfo (name = "isSolved")val estResolu : Boolean,
    val suspect: String = "",
    val nomFichierPhoto: String? = null
)
