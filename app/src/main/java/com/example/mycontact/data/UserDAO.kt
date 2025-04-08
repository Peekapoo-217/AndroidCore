package com.example.mycontact.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.entities.PhoneNumber
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Transaction
    @Query("SELECT * FROM user_table")
    fun getAllUsers(): Flow<List<ContactWithPhones>>

    @Query("SELECT * FROM user_table")
    suspend fun getAllRawContacts(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contacts: List<Contact>)

    @Insert
    suspend fun insertOne(contact: Contact): Long

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllContacts()

    @Query("SELECT * FROM phone_table WHERE number = :phone")
    suspend fun findPhoneNumber(phone: String): PhoneNumber?



}