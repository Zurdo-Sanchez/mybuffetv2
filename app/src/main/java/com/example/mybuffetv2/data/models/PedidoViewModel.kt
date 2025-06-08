package com.example.mybuffetv2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybuffetv2.model.ProductoPedido
import com.example.mybuffetv2.model.EventoSeleccionadoManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class PedidoViewModel : ViewModel() {

    private val productosCollection = FirebaseFirestore.getInstance().collection("productos")
    private val firestore = FirebaseFirestore.getInstance()
    private val _productos = MutableStateFlow<List<ProductoPedido>>(emptyList())

    val productos: StateFlow<List<ProductoPedido>> = _productos

    val productosEvento: StateFlow<List<ProductoPedido>> = _productos
        .map { lista ->
            val eventoId = EventoSeleccionadoManager.eventoSeleccionado?.id
            lista.filter { it.eventoId == eventoId }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
                        precio = doc.getDouble("precio") ?: 0.0,
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

    fun registrarPedidos(
        listaPedidos: List<ProductoPedido>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ventasCollection = firestore.collection("ventas")
        var pendientes = listaPedidos.size
        if (pendientes == 0) {
            onSuccess() // nada que registrar
            return
        }
        listaPedidos.forEach { pedido ->
            val ref = ventasCollection.document()
            val pedidoConId = pedido.copy(id = ref.id)
            ref.set(pedidoConId)
                .addOnSuccessListener {
                    pendientes--
                    if (pendientes == 0) {
                        onSuccess()
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        }
    }


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
