package com.example.mycontact.gui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mycontact.entities.Contact
import com.example.mycontact.viewmodel.ContactViewModel
import com.example.mycontact.utils.Validators
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch


@Composable
fun AddContactScreen(viewModel: ContactViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var isPhoneFocused by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var duplicateError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Tên liên hệ") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneError = when {
                    it.isBlank() -> null
                    !Validators.startWithZero(it) -> "Số điện thoại phải bắt đầu bằng 0"
                    else -> null
                }
            },
            label = { Text("Số điện thoại") },
            isError = phoneError != null,
            modifier = Modifier
                .onFocusChanged { focusState ->
                    isPhoneFocused = focusState.isFocused
                    if (!focusState.isFocused) {
                        phoneError = when {
                            phoneNumber.isBlank() -> null
                            !Validators.startWithZero(phoneNumber) -> "Số điện thoại phải bắt đầu bằng số 0"
                            !Validators.hasAtLeast9Digits(phoneNumber) -> "Số điện thoại phải có ít nhất 9 chữ số"
                            else -> null
                        }
                    }
                }
        )

        phoneError?.let {
            Text(it, color = MaterialTheme.colorScheme.error) // Hiện lỗi dưới TextField
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && phoneNumber.isNotBlank() && phoneError == null) {
                    scope.launch {
                        val exists = viewModel.isPhoneNumberExists(phoneNumber)
                        if (exists) {
                            phoneError = "Số điện thoại đã tồn tại"
                        } else {
                            // Tạo Contact mớ   i
/*                            val newContact = Contact(
                                id = 0, // Room sẽ tự generate ID
                                name = name,
                                phoneNumber = listOf(phoneNumber)
                            )*/
                            val newContact = Contact(id = 0, name = name)

                            viewModel.insertContact(newContact, listOf(phoneNumber))

                            name = ""
                            phoneNumber = ""

                            // Quay lại màn trước
                            onBack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm liên hệ")
        }

    }
}
