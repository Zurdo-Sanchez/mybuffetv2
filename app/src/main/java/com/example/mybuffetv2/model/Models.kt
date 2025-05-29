package com.example.mybuffetv2.model

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
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val imagenUrl: String = ""
)

data class Usuario(
    val id: String = "",
    val email: String = "",
    val nombre: String = ""
)

