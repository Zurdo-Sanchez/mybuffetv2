package com.example.mybuffetv2.ui.screens.productos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mybuffetv2.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    productoId: String,
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var coste by remember { mutableStateOf("") }          // NUEVO campo coste
    var imagenUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Cargar datos incluyendo coste
    LaunchedEffect(productoId) {
        try {
            val doc = db.collection("productos").document(productoId).get().await()
            if (doc.exists()) {
                val p = doc.toObject(Producto::class.java)
                if (p != null) {
                    nombre = p.nombre
                    cantidad = p.cantidad.toString()
                    precio = p.precio.toString()
                    coste = p.coste.toString()           // cargar coste
                    imagenUrl = p.imagenUrl
                }
            } else {
                errorMsg = "Producto no encontrado"
            }
        } catch (e: Exception) {
            errorMsg = "Error cargando producto: ${e.message}"
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { onCancelarClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (errorMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMsg ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it.filter { char -> char.isDigit() } },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = coste,
                onValueChange = { coste = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("Coste") },                    // Nuevo campo editable coste
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = imagenUrl,
                onValueChange = { imagenUrl = it },
                label = { Text("URL Imagen") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMsg = "El nombre no puede estar vacío"
                        return@Button
                    }
                    val cantInt = cantidad.toIntOrNull() ?: 0
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val costeDouble = coste.toDoubleOrNull() ?: 0.0

                    val updates = hashMapOf<String, Any>(
                        "nombre" to nombre,
                        "cantidad" to cantInt,
                        "precio" to precioDouble,
                        "coste" to costeDouble,          // incluir en actualización
                        "imagenUrl" to imagenUrl
                    )

                    db.collection("productos").document(productoId)
                        .update(updates)
                        .addOnSuccessListener {
                            errorMsg = null
                            onGuardarClick()
                        }
                        .addOnFailureListener {
                            errorMsg = "Error guardando producto: ${it.message}"
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            errorMsg?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
