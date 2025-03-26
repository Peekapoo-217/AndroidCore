package com.example.mycontact.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeActionBackground(direction: SwipeToDismissBoxValue) {
    val backgroundColor by animateColorAsState(
        targetValue = when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> Color.Yellow
            SwipeToDismissBoxValue.EndToStart -> Color.Red
            else -> Color.Transparent
        },
        label = "backgroundColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = if (direction == SwipeToDismissBoxValue.StartToEnd)
                Arrangement.Start else Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sửa", color = Color.Black)
            } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                Text("Xóa", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.White)
            }
        }
    }
}
