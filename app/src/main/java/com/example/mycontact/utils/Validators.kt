package com.example.mycontact.utils

object Validators {
    /*    fun isValidPhoneNumber(phone:String): Boolean{
            val regex = Regex("^0\\d{8,}$")
            return regex.matches(phone)
        }*/
    fun startWithZero(phone: String): Boolean {
        return phone.startsWith("0")
    }

    fun hasAtLeast9Digits(phone: String): Boolean {
        return phone.length >= 9 && phone.all { it.isDigit() }
    }
}