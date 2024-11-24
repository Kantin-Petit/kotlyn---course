package com.example.criminalintent_2024.bd

import androidx.room.TypeConverter
import java.util.Date

class ConvTypeIncident {
    @TypeConverter
    fun deDate(date: Date): Long{
        return date.time
    }

    @TypeConverter
    fun versDate(duree:Long): Date {
        return Date(duree)
    }
}