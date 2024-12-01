package com.example.goodtimestamp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.goodtimestamp.GoodTimestampApplication
import com.example.goodtimestamp.databinding.ActivityDiaryDetailBinding
import com.example.goodtimestamp.viewmodel.DiaryViewModel
import com.example.goodtimestamp.viewmodel.DiaryViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryDetailBinding
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory((application as GoodTimestampApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val diaryId = intent.getLongExtra("diary_id", -1)
        if (diaryId != -1L) {
            loadDiary(diaryId)
        }

        setupActionBar()
        setupEditButton()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "時間記錄"
        }
    }

    private fun loadDiary(id: Long) {
        lifecycleScope.launch {
            viewModel.loadEntry(id)
            viewModel.currentEntry.collect { entry ->
                entry?.let {
                    binding.textTitle.text = it.title
                    binding.textContent.text = it.content
                    binding.textDate.text = formatDate(it.createDate)
                }
            }
        }
    }

    private fun setupEditButton() {
        binding.fabEdit.setOnClickListener {
            viewModel.currentEntry.value?.let { entry ->
                val intent = Intent(this, DiaryEditActivity::class.java).apply {
                    putExtra("diary_id", entry.id)
                }
                startActivity(intent)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}