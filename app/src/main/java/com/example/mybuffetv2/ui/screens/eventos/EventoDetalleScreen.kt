package com.example.mybuffetv2.ui.screens.eventos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybuffetv2.model.Evento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetalleScreen(
    eventoId: String,
    onVolver: () -> Unit,
    onEventoActualizado: () -> Unit,
    onEventoBorrado: () -> Unit,
    onIrAlBuffet: () -> Unit,
    onVerRecaudacion: () -> Unit,
    onProductos: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var evento by remember { mutableStateOf<Evento?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var mostrandoMensaje by remember { mutableStateOf<String?>(null) }
    var editandoNombre by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var mostrarConfirmarBorrar by remember { mutableStateOf(false) }
    var mostrarConfirmarTerminar by remember { mutableStateOf(false) }
    var menuExpandido by remember { mutableStateOf(false) }
    var recaudacion by remember { mutableStateOf(200) } // Lo podés cargar dinámico si querés

    // Función para cargar evento desde Firestore
    fun cargarEvento() {
        cargando = true
        db.collection("eventos").document(eventoId).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val ev = doc.toObject(Evento::class.java)
                    if (ev != null) {
                        evento = ev.copy(id = doc.id)
                        nombre = ev.nombre
                    }
                }
                cargando = false
            }
            .addOnFailureListener {
                mostrandoMensaje = "Error al cargar el evento"
                cargando = false
            }
    }

    // Cargar evento al inicio
    LaunchedEffect(Unit) {
        cargarEvento()
    }

    // Mostrar mensaje temporizado
    LaunchedEffect(mostrandoMensaje) {
        if (mostrandoMensaje != null) {
            delay(3000)
            mostrandoMensaje = null
        }
    }

    if (evento == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (mostrarConfirmarBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarBorrar = false },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Estás seguro de que querés borrar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cargando = true
                        mostrarConfirmarBorrar = false
                        val data = hashMapOf<String, Any>("estado" to 8)
                        db.collection("eventos").document(eventoId)
                            .update(data)
                            .addOnSuccessListener {
                                cargando = false
                                mostrandoMensaje = "Evento borrado"
                                onEventoBorrado()
                                cargarEvento()
                            }
                            .addOnFailureListener {
                                cargando = false
                                mostrandoMensaje = "Error al marcar como borrado"
                            }
                    }
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarBorrar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarConfirmarTerminar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarTerminar = false },
            title = { Text("Confirmar terminar evento") },
            text = { Text("¿Querés terminar este evento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cargando = true
                        mostrarConfirmarTerminar = false
                        val data = hashMapOf<String, Any>("estado" to 0)
                        db.collection("eventos").document(eventoId)
                            .update(data)
                            .addOnSuccessListener {
                                cargando = false
                                mostrandoMensaje = "Evento terminado"
                                onEventoActualizado()
                                cargarEvento()
                            }
                            .addOnFailureListener {
                                cargando = false
                                mostrandoMensaje = "Error al terminar el evento"
                            }
                    }
                ) {
                    Text("Terminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarTerminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Nombre y menú settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (editandoNombre) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { if (it.length <= 50) nombre = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (nombre.isNotBlank()) {
                            cargando = true
                            val data = hashMapOf<String, Any>("nombre" to nombre)
                            db.collection("eventos").document(eventoId)
                                .update(data)
                                .addOnSuccessListener {
                                    cargando = false
                                    mostrandoMensaje = "Nombre actualizado"
                                    editandoNombre = false
                                    onEventoActualizado()
                                    cargarEvento()
                                }
                                .addOnFailureListener {
                                    cargando = false
                                    mostrandoMensaje = "Error al actualizar el nombre"
                                }
                        }
                    }),
                    enabled = !cargando
                )
            } else {
                Text(
                    text = nombre,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
                if (evento!!.estado != 8) {
                    Box {
                        IconButton(onClick = { menuExpandido = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Configuración")
                        }
                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            if (evento!!.estado == 1) {
                                DropdownMenuItem(
                                    text = { Text("Editar nombre") },
                                    onClick = {
                                        menuExpandido = false
                                        editandoNombre = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Borrar evento") },
                                onClick = {
                                    menuExpandido = false
                                    mostrarConfirmarBorrar = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Borrar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Recaudación €$recaudacion",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = "ACCIONES",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (evento!!.estado == 1) {
                AccionCuadro(
                    icon = Icons.Filled.ShoppingCart,
                    texto = "Ir al Buffet",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onIrAlBuffet
                )
            }

            AccionCuadro(
                icon = Icons.Filled.Add,
                texto = "Productos",
                modifier = Modifier.fillMaxWidth(),
                onClick = onProductos
            )

            AccionCuadro(
                icon = Icons.Filled.ExitToApp,
                texto = "Ver Recaudación",
                modifier = Modifier.fillMaxWidth(),
                onClick = onVerRecaudacion
            )

            if (evento!!.estado == 1) {
                AccionCuadro(
                    icon = Icons.Filled.Done,
                    texto = "Terminar Evento",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { mostrarConfirmarTerminar = true }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (mostrandoMensaje != null) {
            Text(
                text = mostrandoMensaje ?: "",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun AccionCuadro(
    icon: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(0.9f)
            ) {
                Icon(icon, contentDescription = texto, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(texto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}
