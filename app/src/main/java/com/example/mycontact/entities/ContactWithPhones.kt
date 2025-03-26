package com.example.mycontact.entities

import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.room.Embedded
import androidx.room.Relation

data class ContactWithPhones(
    @Embedded val contact: Contact,
    @Relation(
        parentColumn = "id",
        entityColumn = "contactId"
    )
    val phone : List<PhoneNumber>
)