package com.example.mycontact.utils
object Validators {
    fun isValidPhoneNumber(phone:String): Boolean{
        val regex = Regex("^0\\d{8,}$")
        return regex.matches(phone)
    }
}