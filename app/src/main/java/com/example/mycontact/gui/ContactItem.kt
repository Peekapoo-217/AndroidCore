package com.example.mycontact.gui

import com.example.mycontact.components.ContactCard
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mycontact.components.SwipeActionBackground
import com.example.mycontact.entities.Contact
import com.example.mycontact.viewmodel.ContactViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(
    contact: Contact,
    viewModel: ContactViewModel,
    onEdit: () -> Unit,
    navController: NavController
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val resetTableDismissState = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.4f },
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    showDeleteDialog = true
                    scope.launch {
                        // Sử dụng biến tham chiếu
                        resetTableDismissState.value?.reset()
                    }
                    true
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    scope.launch {
                        // Sử dụng biến tham chiếu
                        resetTableDismissState.value?.reset()
                    }
                    true
                }

                SwipeToDismissBoxValue.Settled -> false
            }

        }
    )

    // Gán dismissState vào biến tham chiếu để sử dụng sau này
    LaunchedEffect(dismissState) {
        resetTableDismissState.value = dismissState
    }

    val currentDirection = dismissState.currentValue

    Box(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        SwipeActionBackground(direction = currentDirection)

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {},
            content = {
                ContactCard(contact = contact) {
                    navController.navigate(Route.contactDetailRoute(contact.id))
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
