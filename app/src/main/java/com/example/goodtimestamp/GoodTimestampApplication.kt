// GoodTimestampApplication.kt
package com.example.goodtimestamp

import android.app.Application
import com.example.goodtimestamp.data.database.DiaryDatabase
import com.example.goodtimestamp.repository.DiaryRepository

class GoodTimestampApplication : Application() {
    private val database by lazy { DiaryDatabase.getDatabase(this) }
    val repository by lazy { DiaryRepository(database.diaryDao()) }
}