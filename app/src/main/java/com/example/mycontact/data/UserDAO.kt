package com.example.mycontact.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mycontact.entities.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<Contact>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)
    @Update
    suspend fun update(contact: Contact)
    @Delete
    suspend fun delete(contact: Contact)
}