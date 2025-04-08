package com.example.mycontact.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
)*/
@Entity(tableName = "user_table")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val systemContactId: String? = null  // <-- dùng để biết mapping
)

