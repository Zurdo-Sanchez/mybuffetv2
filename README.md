# 🍽️ MyBuffetV2

**MyBuffetV2** es una app móvil para Android que permite organizar y registrar las compras de productos en eventos sociales como despedidas, fiestas o reuniones familiares. Cada evento tiene sus propios productos, usuarios y compras, con toda la información guardada en Firebase Firestore.

---

## 📱 Características principales

- 📋 **Gestión de eventos**: creá, visualizá, cerrá o archivá eventos.
- 🛍️ **Control de productos**: agregá productos con precio, cantidad e imagen.
- 🛒 **Registro de compras**: seleccioná productos y guardá compras por usuario.
- 🔐 **Login con Firebase Auth**: cada usuario ve sus eventos.
- ☁️ **Sincronización en la nube**: todo se guarda en Firestore en tiempo real.
- ♻️ **Recuperación de datos**: productos y eventos no se borran, solo se "archivan" cambiando su estado.
- 🖼️ **Diseño adaptable** con Jetpack Compose y Material 3.

---

## 🧠 Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Firebase Firestore
- Firebase Authentication
- Navigation Compose
- State Management con ViewModels y Managers compartidos

---

## 📂 Estructura del proyecto

```
com.example.mybuffetv2/
│
├── model/                  # Modelos de datos
├── ui/
│   ├── screens/            # Pantallas de la app
│   ├── theme/              # Colores, tipografías, estilos
│   └── navigation/         # Controlador de navegación
├── utils/                  # Helpers y utilidades
└── MyBuffetV2App.kt        # Entry point
```

---

## 🧭 Navegación de pantallas

| Pantalla                | Descripción                                      |
|------------------------|--------------------------------------------------|
| Dashboard              | Lista de eventos, crear nuevo, cerrar o borrar  |
| AgregarEvento          | Crear evento nuevo con nombre y personas         |
| Productos              | Lista de productos del evento actual             |
| Agregar/Editar Producto| Crear o editar productos                         |
| Compras                | Selección de productos, total y registro         |
| Login (Firebase)       | Autenticación con correo electrónico             |

---

## 🔒 Seguridad

- Los eventos tienen un campo `usuarios` con UIDs permitidos.
- Las reglas de Firestore (sugeridas) restringen el acceso a los documentos relacionados a esos UIDs.

---

## 🗃️ Modelo de datos en Firestore

### `eventos`
```json
{
  "nombre": "Despedida Juan",
  "descripcion": "Fiesta en casa",
  "ctdPersonas": 20,
  "estado": 0,
  "usuarios": ["uid1", "uid2"]
}
```

### `productos`
```json
{
  "nombre": "Cerveza",
  "precio": 500.0,
  "cantidad": 12,
  "imagenUrl": "...",
  "eventoId": "idEvento",
  "estado": 1
}
```

### `compras`
```json
{
  "productoId": "idProducto",
  "usuarioId": "uid1",
  "eventoId": "idEvento",
  "cantidad": 3,
  "fecha": Timestamp
}
```

---

## 🚀 Futuras funcionalidades

- Notificaciones push
- Exportación a PDF o Excel
- Modo offline con sincronización
- Estadísticas de consumo
- Interfaz para smartwatch

---

## 🧑‍💻 Instalación y ejecución

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tuusuario/MyBuffetV2.git
   ```

2. Abrir en Android Studio.

3. Configurar Firebase:
   - Crear un proyecto Firebase.
   - Agregar app Android.
   - Descargar `google-services.json` y ponerlo en `app/`.

4. Compilar y ejecutar en un emulador o dispositivo físico.

---

## ❤️ Autor

Desarrollado por **Juan Manuel** con amor, dedicación y mate.  
Feedback, ideas o mejoras son bienvenidas.

---

## 📜 Licencia

Este proyecto está bajo la licencia MIT.  
Podés usarlo, modificarlo y compartirlo libremente.
