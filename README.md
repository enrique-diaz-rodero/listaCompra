# 🛒 Lista Compra Colaborativa

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple?style=flat-square&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Modern-green?style=flat-square&logo=android)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Analytics-orange?style=flat-square&logo=firebase)
![Open Food Facts](https://img.shields.io/badge/API-Open%20Food%20Facts-blue?style=flat-square)

Una aplicación Android nativa y moderna para gestionar la lista de la compra de forma colaborativa. Diseñada para ser utilizada por múltiples usuarios en tiempo real, con integración de APIs externas para la búsqueda de productos, imágenes y precios.

> **📱 Nota para la evaluación:** Para probar la aplicación sin necesidad de compilar el código, puedes descargar el archivo **APK adjunto en la entrega** e instalarlo directamente en cualquier dispositivo Android.

## ✨ Características Principales

*   **Autenticación Segura:** Inicio de sesión con cuenta de Google o de forma anónima mediante Firebase Auth.
*   **Sincronización en Tiempo Real:** Las listas y los productos se actualizan instantáneamente en todos los dispositivos conectados gracias a los Listeners de Firestore.
*   **Listas Compartidas:** Posibilidad de invitar a otros usuarios a una lista mediante su correo electrónico. Gestión de miembros (añadir y expulsar).
*   **Búsqueda Inteligente (Open Food Facts):** Buscador integrado que sugiere productos con sus imágenes reales, filtrado por el país e idioma del dispositivo y optimizado con caché local.
*   **Consulta de Precios (Open Prices):** Estimación de precios basada en el código de barras (EAN) del producto y el supermercado preferido del usuario.
*   **Auto-clasificación:** Diccionario local inteligente que asigna automáticamente el producto a una de las 15 categorías base (Lácteos, Carnicería, Limpieza, etc.).
*   **Categorías Predeterminadas:** Al crear una lista, se generan automáticamente las secciones de un supermercado típico y una categoría comodín ("Sin categoría").
*   **Preferencias del Usuario:** Cuestionario inicial (Onboarding) y ajustes guardados localmente con DataStore para personalizar la experiencia (supermercado favorito, mostrar/ocultar imágenes y precios).
*   **Analítica:** Integración de Firebase Analytics para el seguimiento de eventos clave (login, creación de listas, añadir productos).

## 🛠️ Stack Tecnológico

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
*   **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture principles.
*   **Inyección de Dependencias:** [Hilt](https://dagger.dev/hilt/)
*   **Backend & Base de Datos:** [Firebase](https://firebase.google.com/) (Authentication, Cloud Firestore, Analytics).
*   **Peticiones de Red:** [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/)
*   **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/)
*   **Persistencia Local:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
*   **Asincronía:** Kotlin Coroutines & Flow.

## 🏗️ Estructura del Proyecto

El proyecto sigue una estructura de paquetes orientada a características y capas:

~~~text
com.enriquediaz.listacompra
├── data
│   ├── model         # Data classes (User, ShoppingList, Category, Product)
│   ├── remote        # Interfaces de Retrofit (OpenFoodFactsApi, OpenPricesApi)
│   └── repository    # Repositorios (Auth, List, ProductSearch, Price, Preferences)
├── di                # Módulos de Hilt (NetworkModule, FirebaseModule)
├── ui
│   ├── auth          # Pantallas de Login y AuthViewModel
│   ├── lists         # Dashboard, Onboarding, SettingsDialog y ViewModels
│   ├── detail        # Detalle de lista (Productos, Categorías, Miembros) y ViewModels
│   ├── navigation    # AppNavigation (NavHost)
│   └── theme         # Colores, Tipografía y Tema de Compose
└── utils             # Diccionario AutoCategorizer
~~~

## 🚀 Instalación y Configuración

Para ejecutar este proyecto localmente en Android Studio, sigue estos pasos:

1.  **Clonar el repositorio:**
    ~~~bash
    git clone https://github.com/tu-usuario/listaCompra.git
    ~~~
2.  **Abrir en Android Studio:** Asegúrate de tener la versión más reciente de Android Studio.
3.  **Configurar Firebase:**
    *   Crea un proyecto en la consola de Firebase.
    *   Añade una aplicación Android con el ID `com.enriquediaz.listacompra`.
    *   Añade tus huellas SHA-1 (necesarias para Google Sign-In).
    *   Descarga el archivo `google-services.json` y colócalo en la carpeta `app/`.
    *   Habilita **Authentication** (Google y Anónimo), **Cloud Firestore** y **Analytics** en la consola de Firebase.
4.  **Sincronizar y Ejecutar:** Sincroniza el proyecto con Gradle y ejecútalo en un emulador o dispositivo físico con Android 7.0 (API 24) o superior.

## 🔐 Reglas de Firestore Utilizadas

Para el correcto funcionamiento del modo compartido y garantizar la seguridad de los datos, se aplicaron las siguientes reglas de seguridad en Firestore:

~~~javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null;
    }
    match /lists/{listId} {
      allow read, write: if request.auth != null;
      match /{subCollection}/{document} {
        allow read, write: if request.auth != null;
      }
    }
  }
}
~~~

## 👥 Autores

*   **Enrique Díaz** - *Desarrollo Backend, Arquitectura y UI* - [Enrique Díaz](https://github.com/enrique-diaz-rodero)
*   **Mamen Busquets** - *Desarrollo Backend, Arquitectura y UI* - [MamenBusquets](https://github.com/MamenBusquets)
