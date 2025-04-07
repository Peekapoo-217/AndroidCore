package com.example.mycontact.utils

object Validators {
    fun startWithZero(phone: String): Boolean {
        return phone.startsWith("0")
    }

    fun hasAtLeast9Digits(phone: String): Boolean {
        return phone.length >= 9 && phone.all { it.isDigit() }
    }
}