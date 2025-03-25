package com.example.personalinfo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalinfo.ui.theme.PersonalInfoTheme

import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonalInfoTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserInfoForm(modifier = Modifier.padding(innerPadding))
                }*/
                UserInfoForm()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoForm() {
    val context = LocalContext.current
    var name by remember { mutableStateOf(TextFieldValue()) }
    var idCard by remember { mutableStateOf(TextFieldValue()) }
    var selectedDegree by remember { mutableStateOf("Đại học") }
    var preferences = remember { mutableStateOf(setOf<String>()) }
    var additionalInfo by remember { mutableStateOf(TextFieldValue()) }
    var showDialog by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    val degrees = listOf("Trung cấp", "Cao đẳng", "Đại học")
    val interests = listOf("Đọc báo", "Đọc sách", "Đọc coding")

    var preferencesError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        SmallTopAppBar(
            title = { Text("Thông tin cá nhân") },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.DarkGray,
                titleContentColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Họ tên:", fontSize = 16.sp)
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.text.length < 3
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.text.length < 3,
                    singleLine = true
                )
                if(nameError) {
                    Text("Tên phải có ít nhất 3 kí tự")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "CMND:", fontSize = 16.sp)
                TextField(
                    value = idCard,
                    onValueChange = {
                        idCard = it
                        nameError = it.text.all{char -> char.isDigit() && it.text.length <= 9}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = idCard.text.length != 9,
                    singleLine = true
                )
                if(nameError){
                    Text("CMND phải có đúng 9 chữ số")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Bằng cấp:", fontSize = 16.sp)
                degrees.forEach { degree ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (degree == selectedDegree),
                            onClick = { selectedDegree = degree }
                        )
                        Text(text = degree)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Sở thích:", fontSize = 16.sp)
                interests.forEach { interest ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = preferences.value.contains(interest),
                            onCheckedChange = {
                                val newPreferences = preferences.value.toMutableSet()
                                if (it) newPreferences.add(interest) else newPreferences.remove(interest)
                                preferences.value = newPreferences

/*                                preferencesError =*/
                            }
                        )
                        Text(text = interest)
                    }
                }

                if (preferencesError) {
                    Text(text = "Phải chọn ít nhất một sở thích", color = Color.Red, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Thông tin bổ sung:", fontSize = 16.sp)
                TextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (name.text.length < 3) {
                            Toast.makeText(context, "Tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show()
                        } else if (idCard.text.length < 9) {
                            Toast.makeText(context, "CMND phải có đúng 9 chữ số", Toast.LENGTH_SHORT).show()
                        } else if (preferences.value.isEmpty()) {
                            Toast.makeText(context, "Phải chọn ít nhất một sở thích", Toast.LENGTH_SHORT).show()
                        } else {
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Gửi thông tin")
                }
            }
        }
    }
    if (showDialog) {
        ResultScreen(
            name = name.text,
            cmnd = idCard.text,
            degree = selectedDegree,
            hobbies = preferences.value.joinToString(", "),
            additional = additionalInfo.text,
            onDismiss = { showDialog = false }
        )
    }
}
@Composable
fun ResultScreen(name: String, cmnd: String, degree: String, hobbies: String, additional: String, onDismiss : () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss()},
        title = { Text("Thông tin cá nhân", color = Color.Blue) },
        text = {
            Column {
                Text(name)
                Text(cmnd)
                Text(degree)
                Text(hobbies)
                Text("------------------------------")
                Text("Thông tin bổ sung:")
                Text(additional)
                Text("------------------------------")
            }
        },
        confirmButton = {
        }
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PersonalInfoTheme {
        Greeting("Android")
    }
}