package com.example.mycontact

import Route
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycontact.gui.AddContactScreen
import com.example.mycontact.gui.ContactDetailScreen
import com.example.mycontact.gui.ContactListScreen
import com.example.mycontact.ui.theme.MyContactTheme
import com.example.mycontact.viewmodel.ContactViewModel
import com.example.mycontact.viewmodel.ContactViewModelFactory

class MainActivity : ComponentActivity() {
/*    private val contactViewModel: ContactViewModel by viewModels()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS
        )

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 1)
        }


        setContent {
            MyContactTheme {
                val navController = rememberNavController()

                val contactViewModel: ContactViewModel = viewModel(factory = ContactViewModelFactory(application))

                // Observe danh bạ từ ViewModel
                val allContacts by contactViewModel.allContacts.observeAsState(emptyList())

                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = { navController.navigate(Route.AddContact) }) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm liên hệ")
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        NavHost(navController = navController, startDestination = Route.ContactList) {

                            composable(Route.ContactList) {
                                ContactListScreen(
                                    viewModel = contactViewModel,
                                    navController = navController,
                                    onImportContact = {
                                        contactViewModel.importSystemContacts(applicationContext)
                                    }
                                )

                            }

                            composable(Route.AddContact) {
                                AddContactScreen(viewModel = contactViewModel, onBack = { navController.popBackStack() })
                            }

                            composable(Route.ContactDetail) { backStackEntry ->
                                val contactId = backStackEntry.arguments?.getString("contactId")?.toIntOrNull()

                                //Tìm contact từ list đã observe
                                val contact = allContacts.find { it.contact.id == contactId }

                                contact?.let {
                                    ContactDetailScreen(
                                        contact = it.contact,
                                        contactWithPhones = it,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

