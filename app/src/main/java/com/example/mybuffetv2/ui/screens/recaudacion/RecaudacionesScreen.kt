package com.example.mybuffetv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybuffetv2.model.EventoSeleccionadoManager.eventoSeleccionado
import com.example.mybuffetv2.viewmodel.RecaudacionViewModel

@Composable
fun RecaudacionScreen(viewModel: RecaudacionViewModel, onVolver: () -> Unit) {
    val ventas by viewModel.ventas.collectAsState()
    var agrupado by remember { mutableStateOf(true) }

    val totalRecaudado = ventas.sumOf { it.total }

    val evento = eventoSeleccionado
    val nombreEvento = evento?.nombre ?: "Sin evento seleccionado"

    LaunchedEffect(Unit) {
        viewModel.cargarVentas()
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Volver")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = nombreEvento,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total recaudado: $${"%.2f".format(totalRecaudado)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Agrupar por producto", modifier = Modifier.weight(1f))
                Switch(checked = agrupado, onCheckedChange = { agrupado = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (agrupado) {
                val productosAgrupados = ventas.groupBy { it.nombre }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    productosAgrupados.forEach { (producto, lista) ->
                        val totalCantidad = lista.sumOf { it.cantidad }
                        val totalPrecio = lista.sumOf { it.total }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(producto, style = MaterialTheme.typography.titleMedium)
                                    Text("Cantidad total: $totalCantidad")
                                    Text("Recaudado: $${"%.2f".format(totalPrecio)}")
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(ventas) { venta ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Producto: ${venta.nombre}")
                                    Text("Precio: $${"%.2f".format(venta.precio)}")
                                }

                                if (evento?.estado == 1) {
                                    IconButton(onClick = {
                                        viewModel.eliminarVenta(venta.id)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar venta"
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
}
