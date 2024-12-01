package com.example.goodtimestamp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.goodtimestamp.GoodTimestampApplication
import com.example.goodtimestamp.databinding.ActivityDiaryEditBinding
import com.example.goodtimestamp.viewmodel.DiaryViewModel
import com.example.goodtimestamp.viewmodel.DiaryViewModelFactory

class DiaryEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryEditBinding
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory((application as GoodTimestampApplication).repository)
    }
    private var diaryId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 從 Intent 獲取日記 ID，如果是編輯模式的話
        diaryId = intent.getLongExtra("diary_id", -1)
        if (diaryId != -1L) {
            loadDiary()
        }

        setupSaveButton()
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (diaryId == -1L) "新增記錄" else "編輯記錄"
        }
    }

    private fun loadDiary() {
        lifecycleScope.launch {
            viewModel.loadEntry(diaryId)
            viewModel.currentEntry.collect { entry ->
                entry?.let {
                    binding.editTitle.setText(it.title)
                    binding.editContent.setText(it.content)
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()

            if (title.isNotBlank() && content.isNotBlank()) {
                lifecycleScope.launch {
                    if (diaryId == -1L) {
                        viewModel.insertEntry(title, content)
                    } else {
                        viewModel.currentEntry.value?.let { entry ->
                            val updatedEntry = entry.copy(
                                title = title,
                                content = content,
                                modifiedDate = System.currentTimeMillis()
                            )
                            viewModel.updateEntry(updatedEntry)
                        }
                    }
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}