package com.example.mybuffetv2.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import androidx.compose.material3.TextFieldDefaults
import com.example.mybuffetv2.model.Evento


enum class EstadoFiltro(val displayName: String, val estadoCodigo: Int) {
    TODAS("Todas", -1),
    ACTIVA("Activa", 1),
    CERRADA("Cerrada", 0),
    BORRADA("Borrada", 8)
}

data class Evento(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val ctdPersonas: Int = 0,
    val estado: Int = 0,
    val usuarios: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    onNuevoClick: () -> Unit,
    onSalirClick: () -> Unit,
    onPreferenciasClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf(EstadoFiltro.ACTIVA) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }

    val db = FirebaseFirestore.getInstance()

    DisposableEffect(Unit) {
        val registration: ListenerRegistration = db.collection("eventos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMsg = "Error al cargar eventos: ${error.message}"
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    eventos = snapshot.documents.map { doc ->
                        Evento(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            descripcion = doc.getString("descripcion") ?: "",
                            ctdPersonas = doc.getLong("ctdPersonas")?.toInt() ?: 0,
                            estado = doc.getLong("estado")?.toInt() ?: 0,
                            usuarios = doc.get("usuarios") as? List<String> ?: emptyList()
                        )
                    }
                    isLoading = false
                    errorMsg = null
                }
            }
        onDispose {
            registration.remove()
        }
    }

    val eventosFiltrados = eventos.filter {
        (filtroSeleccionado == EstadoFiltro.TODAS || it.estado == filtroSeleccionado.estadoCodigo) &&
                it.nombre.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Buffet V2",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Preferencias") },
                            onClick = {
                                menuExpanded = false
                                onPreferenciasClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            onClick = {
                                menuExpanded = false
                                onLogoutClick()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar eventos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EstadoFiltro.values().forEach { estado ->
                            FilterChip(
                                selected = filtroSeleccionado == estado,
                                onClick = { filtroSeleccionado = estado },
                                label = { Text(estado.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        isLoading -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }

                        errorMsg != null -> Text(
                            text = errorMsg ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(12.dp)
                        )

                        eventosFiltrados.isEmpty() -> Text(
                            "No hay eventos para mostrar.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )

                        else -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(eventosFiltrados.size) { index ->
                                val evento = eventosFiltrados[index]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable {
                                            // Seleccionar el evento y navegar
                                            EventoSeleccionadoManager.seleccionarEvento(evento)
                                            navController.navigate("eventoDetalle")
                                        },
                                    elevation = CardDefaults.cardElevation(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            text = evento.nombre,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FloatingActionButton(
                        onClick = onNuevoClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    FloatingActionButton(
                        onClick = onSalirClick,
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Salir",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    )
}
