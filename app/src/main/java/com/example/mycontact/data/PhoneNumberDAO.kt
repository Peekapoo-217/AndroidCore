package com.example.mycontact.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mycontact.entities.PhoneNumber

@Dao
interface PhoneNumberDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(phone: PhoneNumber)
    @Query("SELECT * FROM phone_table WHERE contactId = :contactId")
    suspend fun getPhoneNumberByContactId(contactId: Int): List<PhoneNumber>
    @Delete
    suspend fun delete(phone: PhoneNumber)
    @Query("DELETE FROM PHONE_TABLE WHERE contactId = :contactId ")
    suspend fun deleteAllPhoneForContact(contactId: Int)
    @Query("SELECT * FROM phone_table WHERE number = :phone LIMIT 1")
    suspend fun findPhoneNumber(phone: String): PhoneNumber?

}