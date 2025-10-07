# Serene

Serene es una aplicación Android escrita en Kotlin y construida con Jetpack Compose. Su propósito es acompañarte con suavidad a registrar actividades y eventos significativos de tu relación, mientras cuidas el tono emocional con visuales cálidos y empáticos.

## Características principales

- **Registro sensible** de actividades o eventos, con notas, emociones y métricas de deseo genuino o intensidad.
- **Visualizaciones serenas** que resumen la evolución de la relación con tarjetas y micro-gráficos minimalistas.
- **Historial editable** que permite revisar, actualizar o eliminar entradas con facilidad.
- **Arquitectura preparada** para sincronizar con Firebase Firestore cuando decidas activar el backend.

## Estructura del proyecto

```
Serene/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/serene/app/
│       │   ├── MainActivity.kt
│       │   └── ui/theme/
│       └── res/
│           ├── values/ (colores, cadenas, temas)
│           └── drawable/ y mipmap/ (iconos)
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Requisitos previos

- Android Studio Giraffe o superior.
- JDK 17 configurado en tu entorno.
- Un dispositivo o emulador con Android 7.0 (API 24) o superior.

## Ejecución

1. Abre Android Studio y selecciona **Open an existing project**.
2. Elige la carpeta `Serene` de este repositorio.
3. Espera a que Gradle sincronice las dependencias.
4. Ejecuta la app en un emulador o dispositivo físico.

> Nota: el repositorio no incluye el archivo `gradle-wrapper.jar`. Android Studio lo generará automáticamente al sincronizar, o puedes ejecutar `gradle wrapper --gradle-version 8.5` si prefieres hacerlo manualmente.

## Próximos pasos para integrar Firebase

El proyecto `serene-ae012` ya está creado en Firebase, pero falta configurarlo en la app. Cuando quieras activar la sincronización:

1. **Registra la app Android en Firebase Console**:
   - Accede a [https://console.firebase.google.com/](https://console.firebase.google.com/) y abre el proyecto `Serene` (`serene-ae012`).
   - Añade una nueva app Android con el `applicationId` `com.serene.app`.
   - Descarga el archivo `google-services.json` y colócalo en `app/`.

2. **Agrega los plugins y dependencias**:
   - En `settings.gradle.kts`, incorpora el repositorio de Google Services si aún no aparece.
   - En `build.gradle.kts` (nivel raíz), añade el plugin `com.google.gms.google-services`.
   - En `app/build.gradle.kts`, aplica el plugin y agrega las dependencias de Firebase necesarias, por ejemplo:
     ```kotlin
     plugins {
         id("com.google.gms.google-services")
     }

     dependencies {
         implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
         implementation("com.google.firebase:firebase-firestore-ktx")
         implementation("com.google.firebase:firebase-auth-ktx") // opcional para futura autenticación
     }
     ```

3. **Inicializa Firebase**:
   - Crea un archivo `SereneApp.kt` o inicializa Firebase en `MainActivity` usando `FirebaseApp.initializeApp(context)`.
   - Reemplaza los métodos de placeholder `saveEntryToFirebase` y `loadEntriesFromFirebase` con llamadas reales a Firestore.

4. **Configura Firestore**:
   - En la consola de Firebase, crea una base de datos Firestore en modo producción o prueba según tus necesidades.
   - Define una colección `diary_entries` con documentos que reflejen el modelo `DiaryEntry`.

5. **Autenticación (futuro)**:
   - Cuando quieras añadir autenticación, habilita el proveedor deseado (por ejemplo, Google o Email/Password) en Firebase Console y actualiza la app con Firebase Auth.

## Personalización visual

La app utiliza una paleta de colores suaves (lavandas, rosas y cielo pastel) pensada para transmitir calma. Puedes ajustar tonos y tipografías en los archivos `Color.kt`, `Theme.kt` y `Type.kt` dentro de `ui/theme`.

## Importante

Actualmente la app mantiene los datos en memoria. Mientras no exista la sincronización con Firebase, las entradas se perderán al cerrar la aplicación. Puedes implementar un almacenamiento local (Room, DataStore) si necesitas persistencia offline antes de conectar la nube.

---

Con cariño y serenidad 💜
