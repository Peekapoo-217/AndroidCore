@file:Suppress("DEPRECATION")

package com.example.bth2

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.emptyLongSet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bth2.ui.theme.BTH2Theme
import androidx.compose.material3.Text as Text1
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BTH2Theme {
                Navigation()
            }
        }
    }
}


@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("jhghg") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text1(
            text = "ĐĂNG NHẬP HỆ THỐNG",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Red,
            modifier = Modifier.padding(top = 50.dp, bottom = 40.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { usernameInput -> username = usernameInput },
            label = { Text1(text = "Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = { passwordInput -> password = passwordInput },
            label = { Text1("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { PasswordTrailingIcon(passwordVisible) { passwordVisible = !passwordVisible } }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if(username == "admin" && password == "123"){
                    navController.navigate("bmi")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
/*            shape = RoundedCornerShape(8.dp)*/
        ) {
            Text1(text = "ĐĂNG NHẬP", fontSize = 18.sp)
        }
    }
    }


@Composable
fun PasswordTrailingIcon(passwordVisible: Boolean, onToggle: () -> Unit) {
    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    IconButton(onClick = onToggle) {
        Icon(imageVector = image, contentDescription = "Toggle password visibility")
    }
}


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("bmi") { BMIScreen(navController) }
    }
}




@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text1(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BTH2Theme {
        Greeting("Android")
    }
}