object Route {
    const val ContactList = "contactList"
    const val AddContact = "addContact"
    const val ContactDetail = "contactDetail/{contactId}"
    const val SendSms = "send_sms"
    fun contactDetailRoute(id: Int): String = "contactDetail/$id"
}