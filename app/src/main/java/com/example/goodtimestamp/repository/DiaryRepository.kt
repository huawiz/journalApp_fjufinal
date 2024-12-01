// repository/DiaryRepository.kt
package com.example.goodtimestamp.repository

import com.example.goodtimestamp.data.dao.DiaryDao
import com.example.goodtimestamp.data.entity.DiaryEntry
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    val allEntries: Flow<List<DiaryEntry>> = diaryDao.getAllEntries()

    suspend fun insert(entry: DiaryEntry) = diaryDao.insert(entry)

    suspend fun update(entry: DiaryEntry) = diaryDao.update(entry)

    suspend fun delete(entry: DiaryEntry) = diaryDao.delete(entry)

    suspend fun getEntry(id: Long) = diaryDao.getEntry(id)
}