package com.example.mybuffetv2.model

import androidx.lifecycle.ViewModel
import com.example.mybuffetv2.model.Producto
import com.google.firebase.firestore.FirebaseFirestore

class ProductoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun agregarProducto(producto: Producto) {
        val ref = firestore.collection("productos").document()
        ref.set(producto.copy(id = ref.id))
            .addOnSuccessListener {
                // opcional: feedback
            }
            .addOnFailureListener {
                // opcional: manejar error
            }
    }
}
