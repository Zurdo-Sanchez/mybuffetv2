package com.example.mybuffetv2.ui.screens.productos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mybuffetv2.model.Producto
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.example.mybuffetv2.model.ProductoSeleccionadoManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    onAgregarProductoClick: () -> Unit,
    onVolverClick: () -> Unit,
    onEditarProducto: (Producto) -> Unit
) {
    val productos = remember { mutableStateListOf<Producto>() }
    val eventoActual = EventoSeleccionadoManager.eventoSeleccionado

    val estadoEvento = eventoActual?.estado ?: 0

    var estadoFiltro by remember { mutableStateOf(1) } // 1 = Activos reales
    var productoAConfirmarBorrar by remember { mutableStateOf<Producto?>(null) }
    var productoAConfirmarRecuperar by remember { mutableStateOf<Producto?>(null) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

    DisposableEffect(eventoActual?.id, estadoFiltro) {
        if (eventoActual == null) {
            productos.clear()
            return@DisposableEffect onDispose { }
        }
        listenerRegistration = FirebaseFirestore.getInstance()
            .collection("productos")
            .whereEqualTo("eventoId", eventoActual.id)
            .whereEqualTo("estado", estadoFiltro)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    productos.clear()
                    for (doc in snapshot.documents) {
                        val p = doc.toObject(Producto::class.java)?.copy(id = doc.id)
                        if (p != null) productos.add(p)
                    }
                }
            }
        onDispose {
            listenerRegistration?.remove()
            listenerRegistration = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = eventoActual?.nombre ?: "",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        floatingActionButton = {
            if (estadoEvento != 0 && estadoEvento != 8) {
                if (estadoFiltro == 1) {
                    FloatingActionButton(
                        onClick = onAgregarProductoClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(16.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar Producto")
                    }
                }
            }
        },
        bottomBar = {
            Surface(
               shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onVolverClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                        .navigationBarsPadding()
                ) {
                    Text("Volver")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Ya no mostramos el nombre del evento aquí porque está arriba en la top bar

            Spacer(Modifier.height(8.dp))

            Text(
                text = "PRODUCTOS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                FilterChip(
                    selected = estadoFiltro == 1,
                    onClick = { estadoFiltro = 1 },
                    label = { Text("Activos") },
                    modifier = Modifier.padding(end = 8.dp)
                )

                FilterChip(
                    selected = estadoFiltro == 8,
                    onClick = { estadoFiltro = 8 },
                    label = { Text("Borrados") }
                )
            }

            Spacer(Modifier.height(8.dp))

            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(productos) { producto ->
                        var expandedMenu by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (producto.imagenUrl.isNotEmpty()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(producto.imagenUrl),
                                        contentDescription = "Imagen del producto",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 16.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = producto.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Cantidad: ${producto.cantidad}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "€${String.format("%.2f", producto.precio)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                                Box {
                                    if (estadoEvento != 0 && estadoEvento != 8) {
                                        IconButton(onClick = { expandedMenu = true }) {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Menú opciones")
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = expandedMenu,
                                        onDismissRequest = { expandedMenu = false }
                                    ) {
                                        if (estadoEvento != 0 && estadoEvento != 8 && producto.estado == 1) {
                                            DropdownMenuItem(
                                                text = { Text("Editar") },
                                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                                onClick = {
                                                    expandedMenu = false
                                                    ProductoSeleccionadoManager.seleccionarProducto(producto)
                                                    onEditarProducto(producto)
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Borrar") },
                                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                                onClick = {
                                                    expandedMenu = false
                                                    productoAConfirmarBorrar = producto
                                                }
                                            )
                                        } else if (producto.estado == 8) {
                                            DropdownMenuItem(
                                                text = { Text("Recuperar") },
                                                leadingIcon = { Icon(Icons.Default.Restore, contentDescription = null) },
                                                onClick = {
                                                    expandedMenu = false
                                                    productoAConfirmarRecuperar = producto
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confirmar borrado
            productoAConfirmarBorrar?.let { producto ->
                AlertDialog(
                    onDismissRequest = { productoAConfirmarBorrar = null },
                    title = { Text("Confirmar borrado") },
                    text = { Text("¿Seguro que querés borrar el producto \"${producto.nombre}\"?") },
                    confirmButton = {
                        TextButton(onClick = {
                            productoAConfirmarBorrar = null
                            FirebaseFirestore.getInstance()
                                .collection("productos")
                                .document(producto.id)
                                .update("estado", 8)
                        }) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { productoAConfirmarBorrar = null }) {
                            Text("No")
                        }
                    }
                )
            }

            // Confirmar recuperación
            productoAConfirmarRecuperar?.let { producto ->
                AlertDialog(
                    onDismissRequest = { productoAConfirmarRecuperar = null },
                    title = { Text("Confirmar recuperación") },
                    text = { Text("¿Querés recuperar el producto \"${producto.nombre}\"?") },
                    confirmButton = {
                        TextButton(onClick = {
                            productoAConfirmarRecuperar = null
                            FirebaseFirestore.getInstance()
                                .collection("productos")
                                .document(producto.id)
                                .update("estado", 1)
                        }) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { productoAConfirmarRecuperar = null }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

