package com.example.mycontact.gui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mycontact.entities.Contact
import com.example.mycontact.viewmodel.ContactViewModel
import com.example.mycontact.utils.Validators


@Composable
fun AddContactScreen(viewModel: ContactViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Tên liên hệ") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it
                                phoneError = if(it.isBlank() || Validators.isValidPhoneNumber(it)){
                                    null
                                }else{
                                    "Số điện thoại phải băt đầu bằng số 0 và ít nhất 9 chữ số"
                                }
                            },
            label = { Text("Số điện thoại") },
            isError = phoneError != null)

        phoneError?.let {
            Text(it, color = MaterialTheme.colorScheme.error) // Hiện lỗi dưới TextField
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && phoneNumber.isNotBlank() && phoneError == null) {
                    // Tạo Contact mới
                    val newContact = Contact(
                        id = 0, // Room sẽ tự generate ID
                        name = name,
                        phoneNumber = listOf(phoneNumber)
                    )

                    // Insert vào database
                    viewModel.insertContact(newContact)

                    // Reset form
                    name = ""
                    phoneNumber = ""

                    // Quay lại màn trước
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm liên hệ")
        }

    }
}
