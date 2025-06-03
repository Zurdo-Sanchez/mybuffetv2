package com.example.mybuffetv2.ui.screens.buffet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybuffetv2.model.EventoSeleccionadoManager.eventoSeleccionado
import com.example.mybuffetv2.model.ProductoPedido
import com.example.mybuffetv2.navigation.Routes
import com.example.mybuffetv2.viewmodel.PedidoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuffetScreen(
    viewModel: PedidoViewModel = viewModel(),
    onVolver: () -> Unit,
) {

    val productos by viewModel.productosEvento.collectAsState()
    val evento = eventoSeleccionado
    val nombreEvento = evento?.nombre ?: "Sin evento seleccionado"

    val cantidades = remember { mutableStateMapOf<String, Int>() }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val total = cantidades.entries.sumOf { (id, cantidad) ->
        val producto = productos.find { it.id == id }
        (producto?.precio ?: 0.0) * cantidad
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = nombreEvento,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Total: €$total",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Button(
                    onClick = {
                        if (total > 0) {
                            showConfirmDialog = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(text = "Finalizar pedido")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(productos) { producto ->
                    val cantidadSeleccionada = cantidades.getOrElse(producto.id) { 0 }

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clickable {
                                cantidades[producto.id] = cantidadSeleccionada + 1
                            },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = producto.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "€${String.format("%.2f", producto.precio)}",
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(onClick = {
                                    if (cantidadSeleccionada > 0) {
                                        cantidades[producto.id] = cantidadSeleccionada - 1
                                    }
                                }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                }
                                Text(text = cantidadSeleccionada.toString(), fontSize = 14.sp)
                                IconButton(onClick = {
                                    cantidades[producto.id] = cantidadSeleccionada + 1
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                   onVolver()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(text = "Volver")
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar pedido") },
                text = {
                    Column {
                        cantidades.entries.filter { it.value > 0 }.forEach { (id, cantidad) ->
                            val producto = productos.find { it.id == id }
                            if (producto != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = producto.nombre,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(0.5f)
                                    )
                                    Text(
                                        text = "x $cantidad",
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(0.2f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "€${String.format("%.2f", producto.precio * cantidad)}",
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(0.3f),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text("Total: €$total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val listaProductosPedido = cantidades.entries.flatMap { (id, cantidad) ->
                                val producto = productos.find { it.id == id }
                                if (producto != null && cantidad > 0) {
                                    List(cantidad) {
                                        ProductoPedido(
                                            id = producto.id,
                                            nombre = producto.nombre,
                                            precio = producto.precio,
                                            cantidad = 1,
                                            eventoId = evento?.id ?: "",
                                            activo = true
                                        )
                                    }
                                } else {
                                    emptyList()
                                }
                            }

                            viewModel.registrarPedidos(
                                listaProductosPedido,
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Pedido registrado correctamente")
                                    }
                                    cantidades.clear()
                                    showConfirmDialog = false
                                },
                                onError = { e ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: ${e.message ?: "Desconocido"}")
                                    }
                                }
                            )
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
