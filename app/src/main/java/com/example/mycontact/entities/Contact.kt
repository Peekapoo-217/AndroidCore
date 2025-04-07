package com.example.mycontact.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
)
