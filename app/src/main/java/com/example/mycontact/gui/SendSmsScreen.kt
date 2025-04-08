package com.example.mycontact.gui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mycontact.utils.sendBulkSms
import com.example.mycontact.viewmodel.ContactViewModel

@Composable
fun SendSmsScreen(viewModel: ContactViewModel, onBack: () -> Unit) {
    val contacts by viewModel.allContacts.observeAsState(emptyList())
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }

    val selectedNumbers = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gửi tin nhắn đến nhiều người", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Nội dung tin nhắn") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Chọn số điện thoại", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(contacts) { contactWithPhones ->
                contactWithPhones.phone.forEach { phone ->
                    val isSelected = selectedNumbers.contains(phone.number)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                if (it) selectedNumbers.add(phone.number)
                                else selectedNumbers.remove(phone.number)
                            }
                        )
                        Text(text = "${contactWithPhones.contact.name} - ${phone.number}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                sendBulkSms(context, selectedNumbers, message)
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Gửi tin nhắn")
        }
    }
}
