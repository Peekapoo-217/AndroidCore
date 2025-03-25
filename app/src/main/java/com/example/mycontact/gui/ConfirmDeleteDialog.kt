package com.example.mycontact.gui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    contactName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận xóa") },
        text = { Text("Bạn có chắc chắn muốn xóa liên hệ \"$contactName\"?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Xóa")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
