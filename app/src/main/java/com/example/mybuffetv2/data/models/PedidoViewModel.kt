package com.example.mybuffetv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybuffetv2.model.ProductoPedido
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {

    private val productosCollection = FirebaseFirestore.getInstance().collection("productos")

    private val _productos = MutableStateFlow<List<ProductoPedido>>(emptyList())
    val productos: StateFlow<List<ProductoPedido>> = _productos

    private var listenerRegistration: ListenerRegistration? = null

    init {
        escucharTodosLosProductos()
    }

    private fun escucharTodosLosProductos() {
        listenerRegistration = productosCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                // manejo de error si querés
                return@addSnapshotListener
            }

            val lista = snapshot.documents.mapNotNull { doc ->
                try {
                    ProductoPedido(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        precio = (doc.getLong("precio") ?: 0L).toInt(),
                        cantidad = (doc.getLong("cantidad") ?: 0L).toInt(),
                        eventoId = doc.getString("eventoId") ?: "",
                        activo = (doc.getLong("estado") ?: 0L) == 1L
                    )
                } catch (e: Exception) {
                    null
                }
            }

            viewModelScope.launch {
                _productos.emit(lista)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
