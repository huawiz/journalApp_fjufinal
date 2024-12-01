package com.example.goodtimestamp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.goodtimestamp.GoodTimestampApplication
import com.example.goodtimestamp.R
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

        setupToolbar()

        val diaryId = intent.getLongExtra("diary_id", -1)
        if (diaryId != -1L) {
            loadDiary(diaryId)
        }

        setupEditButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.title_detail)
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
                    binding.textIntensity.text = it.intensity.toString()
                    binding.textEnergy.text = it.energy.toString()
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
        finish()
        return true
    }
}