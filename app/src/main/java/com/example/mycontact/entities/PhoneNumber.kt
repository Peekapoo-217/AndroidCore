package com.example.mycontact.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "phone_table",
    foreignKeys = [ForeignKey(
        entity = Contact::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactId: Int,
    val number: String
)
