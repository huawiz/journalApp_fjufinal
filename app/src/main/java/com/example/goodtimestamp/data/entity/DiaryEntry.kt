package com.example.goodtimestamp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var title: String,
    var content: String,
    var createDate: Long = System.currentTimeMillis(),
    var modifiedDate: Long = System.currentTimeMillis()
)