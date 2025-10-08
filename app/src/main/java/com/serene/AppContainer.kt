package com.serene

import android.content.Context
import com.serene.data.DiaryRepository
import com.serene.data.InMemoryDiaryDataSource

@Suppress("unused_parameter")
class AppContainer(context: Context) {
    /**
     * Cambia esta implementación por `FirebaseDiaryDataSource(Firebase.firestore)` cuando
     * quieras persistir los datos en Firestore.
     */
    val diaryRepository: DiaryRepository = DiaryRepository(InMemoryDiaryDataSource())
}
