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
import com.example.mybuffetv2.viewmodel.PedidoViewModel
import com.example.mybuffetv2.model.EventoSeleccionadoManager.eventoSeleccionado

@Composable
fun BuffetScreen(
    viewModel: PedidoViewModel = viewModel(),
) {
    val productos by viewModel.productos.collectAsState()
    val evento = eventoSeleccionado
    val nombreEvento = evento?.nombre ?: "Sin evento seleccionado"

    // Guardamos cantidades por producto id
    val cantidades = remember { mutableStateMapOf<String, Int>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = nombreEvento,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Totalizador y botón finalizar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            // Total a la izquierda
            val total = cantidades.entries.sumOf { (id, cantidad) ->
                val producto = productos.find { it.id == id }
                (producto?.precio ?: 0) * cantidad
            }
            Text(
                text = "Total: €$total",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Botón a la derecha
            Button(
                onClick = {
                    // Acción para finalizar pedido
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
            modifier = Modifier.fillMaxSize()
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
                            text = "€${producto.precio}",
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
    }
}
