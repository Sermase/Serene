package com.serene

import android.content.Context
// Importaciones que faltan
import com.serene.data.FirebaseDiaryDataSource // Asumiendo que esta clase está en el paquete 'data'
import com.google.firebase.firestore.FirebaseFirestore
// Fin de las importaciones
import com.serene.data.DiaryRepository
import com.serene.data.InMemoryDiaryDataSource

@Suppress("unused_parameter")
class AppContainer(context: Context) {
    /**
     * Cambia esta implementación por `FirebaseDiaryDataSource(Firebase.firestore)` cuando
     * quieras persistir los datos en Firestore.
     */
    val diaryRepository: DiaryRepository = DiaryRepository(
        FirebaseDiaryDataSource(FirebaseFirestore.getInstance())
    )
}
