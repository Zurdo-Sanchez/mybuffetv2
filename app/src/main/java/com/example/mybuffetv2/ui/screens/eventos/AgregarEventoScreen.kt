package com.example.mybuffetv2.ui.screens.eventos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mybuffetv2.model.Evento
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEventoScreen(
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    var ctdPersonasText by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    fun guardarEventoFirestore(evento: Evento, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("eventos").document() // Genera ID automático

        val eventoConId = evento.copy(
            id = docRef.id,
            estado = 1 // Hardcodeamos estado a 1 al guardar
        )

        docRef.set(eventoConId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Agregar Evento",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del evento") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ctdPersonasText,
            onValueChange = {
                if (it.all { c -> c.isDigit() }) ctdPersonasText = it
            },
            label = { Text("Cantidad de personas") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onCancelarClick,
                modifier = Modifier.weight(1f),
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    isSaving = true
                    errorMessage = null

                    val ctdPersonas = ctdPersonasText.toIntOrNull() ?: 0
                    val evento = Evento(
                        id = "",
                        nombre = nombre,
                        descripcion = descripcion,
                        ctdPersonas = ctdPersonas,
                        estado = 0, // Será forzado a 1 en guardado
                        usuarios = emptyList()
                    )

                    guardarEventoFirestore(evento,
                        onSuccess = {
                            isSaving = false
                            onGuardarClick()
                        },
                        onError = { e ->
                            isSaving = false
                            errorMessage = "Error guardando evento: ${e.message}"
                        }
                    )
                },
                enabled = nombre.isNotBlank() && !isSaving,
                modifier = Modifier.weight(1f)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}
