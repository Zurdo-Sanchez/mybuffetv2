package com.example.mybuffetv2.ui.screens.productos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybuffetv2.model.Producto
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.example.mybuffetv2.model.ProductoViewModel
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    onVolver: () -> Unit,
    onProductoAgregado: () -> Unit
) {
    val productoViewModel: ProductoViewModel = viewModel()

    val eventoActual = EventoSeleccionadoManager.eventoSeleccionado

    var nombre by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agregar Producto") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = costo,
                onValueChange = { costo = it },
                label = { Text("Costo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio de venta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val nuevoProducto = Producto(
                            nombre = nombre.trim(),
                            coste = costo.toDoubleOrNull() ?: 0.0,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            eventoId = eventoActual?.id ?: ""
                        )
                        productoViewModel.agregarProducto(nuevoProducto)
                        onProductoAgregado()
                    },
                    enabled = nombre.isNotBlank() && costo.toDoubleOrNull() != null &&
                            cantidad.toIntOrNull() != null && precio.toDoubleOrNull() != null
                ) {
                    Text("Guardar")
                }

                OutlinedButton(onClick = onVolver) {
                    Text("Volver")
                }
            }
        }
    }
}
