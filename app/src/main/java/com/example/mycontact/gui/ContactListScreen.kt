package com.example.mycontact.gui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycontact.viewmodel.ContactViewModel
import com.example.mycontact.entities.ContactWithPhones

@Composable
fun ContactListScreen(viewModel: ContactViewModel, navController: NavController) {
    var selectedContact by remember { mutableStateOf<ContactWithPhones?>(null) }

    val allContacts by viewModel.allContacts.observeAsState(emptyList())

    val searchQuery by viewModel.searchQuery.collectAsState()

    val filteredContacts = viewModel.getFilteredContacts(allContacts)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Danh Bạ Điện Thoại", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Tìm kiếm liên hệ") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(filteredContacts) { contactWithPhones ->
                ContactItem(
                    contact = contactWithPhones.contact,
                    phoneList = contactWithPhones.phone.map { it.number },
                    viewModel = viewModel,
                    onEdit = { selectedContact = contactWithPhones },
                    navController = navController
                )
            }
        }

    }
    selectedContact?.let {
        EditContactDialog(
            contact = it.contact,
            phoneList = it.phone.map { phone -> phone.number },
            viewModel = viewModel,
            onDismiss = { selectedContact = null }
        )
    }
}

