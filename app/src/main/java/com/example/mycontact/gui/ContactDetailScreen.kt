package com.example.mycontact.gui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycontact.entities.Contact
import com.example.mycontact.entities.ContactWithPhones
import com.example.mycontact.utils.isValidPhoneNumber
import com.example.mycontact.utils.makeCall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    contactWithPhones: ContactWithPhones,
    navController: NavController
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết liên hệ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Tên: ${contact.name}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            contactWithPhones.phone
                .filter { isValidPhoneNumber(it.number) }
                .forEach { phone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(phone.number, modifier = Modifier.weight(1f))

                        Button(onClick = { makeCall(context, phone.number) }) {
                            Text("Gọi")
                        }
                    }
                }
        }
    }
}
