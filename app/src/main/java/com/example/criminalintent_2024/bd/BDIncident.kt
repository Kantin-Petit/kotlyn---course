package com.example.criminalintent_2024.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent_2024.Crime

@Database(entities = [Crime::class], version = 3)
@TypeConverters(ConvTypeIncident::class)
abstract class BDIncident : RoomDatabase(){
    abstract fun crimeDAO(): CrimeDAO
}

val migration_1_2 = object : Migration(1,2) {
    override fun migrate(bd: SupportSQLiteDatabase) {
        bd.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}

val migration_2_3 = object : Migration(2,3) {
    override fun migrate(bd: SupportSQLiteDatabase) {
        bd.execSQL(
            "ALTER TABLE Crime ADD COLUMN nomFichierPhoto TEXT"
        )
    }
}