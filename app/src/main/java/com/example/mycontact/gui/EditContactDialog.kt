package com.example.mycontact.gui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mycontact.entities.Contact
import com.example.mycontact.viewmodel.ContactViewModel

@Composable
fun EditContactDialog(contact: Contact, viewModel: ContactViewModel, onDismiss: () -> Unit) {
    var newName by remember { mutableStateOf(contact.name) }
    var newPhones by remember { mutableStateOf(contact.phoneNumber.toMutableStateList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa liên hệ") },
        text = {
            Column {
                // Ô nhập tên liên hệ
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Tên liên hệ") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Danh sách số điện thoại
                newPhones.forEachIndexed { index, phone ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = phone,
                            onValueChange = { newPhones[index] = it }, // Cập nhật số điện thoại tại vị trí index
                            label = { Text("Số điện thoại ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { newPhones.removeAt(index) }) {
                            Text("Xóa")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Nút thêm số điện thoại mới
                Button(
                    onClick = { newPhones.add("") }, // Thêm số mới rỗng vào danh sách
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Thêm số điện thoại")
                }
            }
        },        confirmButton = {
            Button(onClick = {
                val updatedContact = Contact(
                    id = contact.id,
                    name = newName,
                    phoneNumber = newPhones.toList()
                )

                viewModel.updateContact(updatedContact)

                onDismiss()
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
