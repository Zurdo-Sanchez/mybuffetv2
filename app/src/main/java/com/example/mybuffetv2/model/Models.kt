package com.example.mybuffetv2.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Evento(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val ctdPersonas: Int = 0,
    val estado: Int = 0,
    val usuarios: List<String> = emptyList()
)

data class Producto(
    val id: String = "",
    val estado: Int= 1,
    val nombre: String = "",
    val precio: Double = 0.0,
    val coste: Double = 0.0,
    val cantidad: Int = 0,
    val imagenUrl: String = "",
    val eventoId: String = ""
)
data class ProductoVenta(
    val id: String = "",
    val id_producto: String = "",
    val id_evento: String = "",
    val precio: Double = 0.0,
    val coste: Double = 0.0,
    val cantidad: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProductoPedido(
    val id: String = "",
    val nombre: String = "",
    val coste: Double = 0.0,
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val eventoId: String = "",
    val activo: Boolean = true,

)

data class Usuario(
    val id: String = "",
    val email: String = "",
    val nombre: String = ""
)

// Singleton para mantener el evento seleccionado con estado mutable de Compose
object EventoSeleccionadoManager {
    var eventoSeleccionado by mutableStateOf<Evento?>(null)
        private set

    fun seleccionarEvento(evento: Evento) {
        eventoSeleccionado = evento
    }

    fun limpiarSeleccion() {
        eventoSeleccionado = null
    }
}

// Singleton para mantener el producto seleccionado con estado mutable de Compose
object ProductoSeleccionadoManager {
    var productoSeleccionado by mutableStateOf<Producto?>(null)
        private set

    fun seleccionarProducto(producto: Producto) {
        productoSeleccionado = producto
    }

    fun limpiarSeleccion() {
        productoSeleccionado = null
    }
}
