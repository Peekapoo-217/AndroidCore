package com.example.bth2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bth2.ui.theme.BTH2Theme

class BMI : ComponentActivity() {
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
fun BMIScreen(navController: NavController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
/*        verticalArrangement = Arrangement.Center,*/
/*        horizontalAlignment = Alignment.CenterHorizontally*/
    ) {
        Text(text = "Tính chỉ số BMI", fontSize = 24.sp, modifier = Modifier.padding(bottom = 20.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weightInput -> weight = weightInput},
            label = { Text("Cân nặng (kg)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { heightInput -> height = heightInput },
            label = { Text("Chiều cao (cm)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val w = weight.toFloatOrNull()
            val h = height.toFloatOrNull()?.div(100)
            if (w != null && h != null && h > 0) {
                val bmi = w / (h * h)
                bmiResult = "Chỉ số BMI: %.2f - %s".format(bmi, getBMICategory(bmi))
            } else {
                bmiResult = "Vui lòng nhập đúng định dạng!"
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Tính BMI")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = bmiResult, fontSize = 18.sp)
    }
}

fun getBMICategory(bmi: Float): String {
    return when {
        bmi < 18 -> "Gầy"
        bmi in 18f..24.9f -> "Bình thường"
        bmi in 25f..29.9f -> "Béo phì độ I"
        bmi in 30f..34.9f -> "Béo phì độ II"
        else -> "Béo phì độ III"
    }
}

@Preview(showBackground = true)
@Composable
fun BMIPreview() {
    BTH2Theme {
        BMIScreen(navController = rememberNavController())
    }
}
