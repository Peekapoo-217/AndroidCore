object Route {
    const val ContactList = "contactList"
    const val AddContact = "addContact"
    const val ContactDetail = "contactDetail/{contactId}"

    fun contactDetailRoute(id: Int): String = "contactDetail/$id"
}