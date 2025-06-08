package com.example.mybuffetv2.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mybuffetv2.model.Compra
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecaudacionViewModel : ViewModel() {

    private val _compras = MutableStateFlow<List<Compra>>(emptyList())
    val ventas: StateFlow<List<Compra>> = _compras

    private var listener: ListenerRegistration? = null

    fun cargarVentas() {
        val evento = EventoSeleccionadoManager.eventoSeleccionado
        val eventoId = evento?.id ?: return

        // Remover listener anterior si existe
        listener?.remove()

        listener = Firebase.firestore.collection("ventas")
            .whereEqualTo("eventoId", eventoId)
            .whereEqualTo("activo", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val lista = snapshot.documents.mapNotNull { doc ->
                    val compra = doc.toObject(Compra::class.java)
                    compra?.copy(id = doc.id)
                }

                _compras.value = lista
            }
    }

    fun eliminarVenta(ventaId: String) {
        Firebase.firestore.collection("ventas")
            .document(ventaId)
            .update("activo", false) 
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove() // Limpiar listener cuando se destruye el ViewModel
    }
}
