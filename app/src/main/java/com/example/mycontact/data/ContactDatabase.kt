package com.example.mycontact.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.mycontact.entities.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ContactDatabase : RoomDatabase(){
    abstract fun contactDAO() : UserDAO

    companion object{
        @Volatile
        private var INSTANCE : ContactDatabase? = null

        fun getDatabase(context: Context) : ContactDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}