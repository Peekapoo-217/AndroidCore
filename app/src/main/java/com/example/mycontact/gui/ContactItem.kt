package com.example.mycontact.gui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycontact.entities.Contact
import com.example.mycontact.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(contact: Contact, viewModel: ContactViewModel, onEdit: () -> Unit, navController: NavController) {

    val firstPhone = contact.phoneNumber.firstOrNull()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showDetailDialog by remember { mutableStateOf(false) }

    val dismissStateMutableRef = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.4f },
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    showDeleteDialog = true
                    scope.launch {
                        // Sử dụng biến tham chiếu
                        dismissStateMutableRef.value?.reset()
                    }
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    scope.launch {
                        // Sử dụng biến tham chiếu
                        dismissStateMutableRef.value?.reset()
                    }
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }

        }
    )

    // Gán dismissState vào biến tham chiếu để sử dụng sau này
    LaunchedEffect(dismissState) {
        dismissStateMutableRef.value = dismissState
    }

    val currentDirection = dismissState.currentValue
    val targetBackgroundColor by animateColorAsState(
        targetValue = when (currentDirection) {
            SwipeToDismissBoxValue.StartToEnd -> Color.Yellow
            SwipeToDismissBoxValue.EndToStart -> Color.Red
            SwipeToDismissBoxValue.Settled -> Color.Transparent
        },
        label = "backgroundColor"
    )

    Box(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Background với màu sắc tùy vào hướng vuốt
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(targetBackgroundColor, RoundedCornerShape(8.dp))
        ) {
            // Nội dung của background - hiển thị icon và text phù hợp
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = if (currentDirection == SwipeToDismissBoxValue.StartToEnd)
                    Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sửa", color = Color.Black)
                } else if (currentDirection == SwipeToDismissBoxValue.EndToStart) {
                    Text("Xóa", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = Color.White
                    )
                }
            }
        }

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {},
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
/*                            navController.navigate("contactDetail/${contact.id}")*/
                            navController.navigate(Route.contactDetailRoute(contact.id))
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(contact.name, style = MaterialTheme.typography.titleMedium)
                        firstPhone?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        )

    }


    // Hộp thoại xác nhận xóa
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            contactName = contact.name,
            onConfirm = {
                viewModel.deleteContact(contact)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}