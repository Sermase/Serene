# Serene

Serene es una aplicación Android escrita en Kotlin y Jetpack Compose que te invita a crear un diario de relación sereno, empático y visualmente suave. Puedes registrar actividades y eventos importantes, asociar emociones, observar tendencias e integrar Firebase Firestore cuando estés listo.

## Características

- **Registro intuitivo** de actividades o eventos con fecha, intensidad/deseo genuino, emociones y notas.
- **Interfaz en tonos suaves** que refuerza la calma y la reflexión.
- **Resumen estadístico** con tendencia de deseo genuino y emociones más frecuentes.
- **Historial navegable** para editar o eliminar recuerdos.
- **Estructura preparada** para exportar/importar datos y conectar con Firebase Firestore.
- **Sincronización controlada** con indicadores de carga y manejo básico de errores.

## Requisitos

- Android Studio Iguana (o superior).
- Gradle 8.5 y JDK 17 (incluidos con Android Studio).
- Un dispositivo o emulador con Android 7.0 (API 24) o superior.

## Puesta en marcha

1. Clona este repositorio y ábrelo con Android Studio.
2. Sincroniza el proyecto (Gradle Sync) para descargar las dependencias.
3. Ejecuta la aplicación en un dispositivo o emulador con el botón **Run**.

## Configuración de Firebase (Firestore + Analytics)

El proyecto ya incluye los plugins y dependencias necesarias (`com.google.gms.google-services`, `firebase-bom`, Firestore, Analytics y las extensiones de corrutinas para Play Services). Solo falta enlazarlo con tu proyecto de Firebase `serene-ae012`.

1. En la [consola de Firebase](https://console.firebase.google.com/) selecciona el proyecto `Serene` (`serene-ae012`).
2. Haz clic en **Añadir aplicación** y elige el icono de Android.
3. Completa el registro con:
   - **Nombre del paquete**: `com.serene`
   - **Apodo de la app**: "Serene Android" (opcional).
   - **SHA-1**: opcional por ahora (solo será necesario cuando habilites autenticación).
4. Descarga el archivo `google-services.json` que te ofrece Firebase y colócalo en la ruta `app/google-services.json` del proyecto.
5. Sincroniza el proyecto para que Gradle incluya la configuración.
6. En la consola de Firebase, abre **Firestore Database** y crea la base de datos en el modo que prefieras. Crea (o deja que la app cree) la colección `diary_entries`.
7. (Opcional) Activa **Firebase Analytics** y verifica los eventos.

### Conectar el repositorio a Firestore

El flujo de datos ahora está encapsulado en un `DiaryDataSource`. Por defecto se utiliza `InMemoryDiaryDataSource`, pero ya tienes lista la implementación `FirebaseDiaryDataSource`.

1. Abre `AppContainer.kt` y sustituye la línea:
   ```kotlin
   val diaryRepository: DiaryRepository = DiaryRepository(InMemoryDiaryDataSource())
   ```
   por:
   ```kotlin
   val diaryRepository: DiaryRepository = DiaryRepository(
       FirebaseDiaryDataSource(FirebaseFirestore.getInstance())
   )
   ```
   Importa `com.google.firebase.firestore.FirebaseFirestore` y `com.serene.data.FirebaseDiaryDataSource`.
2. Lanza la app. `SereneApplication` inicializará Firebase y el `FirebaseDiaryDataSource` añadirá un listener en tiempo real.
3. Los métodos `add`, `update`, `delete` e `import` ya usan escrituras por lotes con corrutinas (`await`), por lo que no necesitas código adicional.
4. Ajusta las reglas de seguridad de Firestore según el entorno (modo prueba en desarrollo, reglas estrictas en producción).

> Consejo: si quieres combinar datos locales y remotos, crea una implementación híbrida que delegue en Firestore y mantenga caché local usando la misma interfaz `DiaryDataSource`.

## Próximos pasos sugeridos

- Implementar persistencia real con Firebase Firestore y sincronización en tiempo real.
- Añadir autenticación con Firebase Authentication cuando se requiera.
- Mejorar la exportación/importación utilizando JSON y el Storage Access Framework.
- Añadir pruebas unitarias para ViewModel y repositorio.

## Estructura principal

```
app/
 ├── build.gradle.kts        # Configuración de la app y dependencias (Compose + Firebase)
 ├── src/main/
 │   ├── AndroidManifest.xml
 │   ├── java/com/serene/
 │   │   ├── MainActivity.kt
 │   │   ├── SereneApplication.kt
 │   │   ├── data/            # Modelos de dominio y repositorio
 │   │   └── ui/              # Componentes Compose y tema
 │   └── res/                 # Recursos (strings, temas, xml)
```

## Licencia

MIT
