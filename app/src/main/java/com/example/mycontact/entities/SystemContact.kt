package com.example.mycontact.entities

data class SystemContact(
    val systemContactId: String,
    val name: String,
    val numbers: List<String>
)